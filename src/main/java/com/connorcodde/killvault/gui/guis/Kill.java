package com.connorcodde.killvault.gui.guis;

import com.connorcodde.killvault.KillVault;
import com.connorcodde.killvault.gui.GuiInterface;
import com.connorcodde.killvault.misc.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.bukkit.Bukkit.getOfflinePlayer;
import static org.bukkit.Bukkit.getServer;

public class Kill implements GuiInterface {
    final int killId;
    boolean empty;
    Inventory inventory;

    public Kill(int killId) {
        this.killId = killId;
    }

    @Override
    public Inventory open(Player player, Inventory inv) throws SQLException, IOException, ClassNotFoundException {
        inventory = inv;
        if (inventory == null || inventory.getSize() != 45)
            inventory = getServer().createInventory(null, 45, Component.text("Vault"));
        empty = Arrays.stream(player.getInventory()
                        .getContents())
                .filter(Objects::nonNull)
                .allMatch(d -> d.getType() == Material.AIR);

        PreparedStatement stmt = KillVault.database.connection.prepareStatement(
                "SELECT dieer, deathInventory, deathMessage, deathTime from  deaths WHERE id = ?");
        stmt.setInt(1, killId);
        ResultSet res = stmt.executeQuery();

        UUID died = UUID.fromString(res.getString(1));
        OfflinePlayer deadPlayer = getOfflinePlayer(died);
        List<ItemStack> diedInventory = Util.inventoryFromBase64(res.getString(2));
        String deathMessage = res.getString(3);
        long deathTime = res.getLong(4);

        Util.setPlayerItem(inventory, 8, deadPlayer, deathMessage, deathTime, -1);
        for (int i : new int[]{4, 5, 6, 7})
            inventory.setItem(i, Util.cleanItemStack(Material.GRAY_STAINED_GLASS_PANE, 1, m -> {
            }));
        int j;

        // Add hot bar items
        j = 36;
        for (int i = 0; i < 9; i++)
            inventory.setItem(j++, diedInventory.get(i));

        // Add Armor / Offhand
        j = 0;
        for (int i = 36; i < 41; i++) inventory.setItem(j++, diedInventory.get(i));

        // Add the rest of inventory
        j = 9;
        for (int i = 9; i < 36; i++) inventory.setItem(j++, diedInventory.get(i));

        return inventory;
    }

    @Override
    public void interact(InventoryClickEvent e) {
        InventoryAction[] invalidActions = new InventoryAction[]{InventoryAction.CLONE_STACK, InventoryAction.UNKNOWN, InventoryAction.COLLECT_TO_CURSOR, InventoryAction.HOTBAR_SWAP};
        InventoryAction[] placeActions = new InventoryAction[]{InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.SWAP_WITH_CURSOR};
        int[] invalidSlots = new int[]{5, 6, 7, 8};

        if (Arrays.stream(invalidSlots)
                .anyMatch(d -> d == e.getSlot()) && e.getClickedInventory() == inventory) {
            e.setCancelled(true);
            return;
        }

        if (empty) return;
        if (Arrays.stream(invalidActions)
                .anyMatch(d -> d == e.getAction()) || ((Arrays.stream(placeActions)
                .anyMatch(d -> d == e.getAction()) && e.getClickedInventory() == inventory)) ||
                (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getClickedInventory() != inventory))
            e.setCancelled(true);
    }

    @Override
    public void close(InventoryCloseEvent e) throws IOException, SQLException {
        // Do the inverse as before to put the current inv state back into the database
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 36; i < 45; i++) itemStacks.add(inventory.getItem(i));
        for (int i = 9; i < 36; i++) itemStacks.add(inventory.getItem(i));
        for (int i = 0; i < 5; i++) itemStacks.add(inventory.getItem(i));

        // Serialize inventor
        String inv = Util.inventoryToBase64(itemStacks);

        // Update Dahtabase
        PreparedStatement stmt = KillVault.database.connection.prepareStatement(
                "UPDATE deaths SET deathInventory = ? WHERE id = ?");
        stmt.setString(1, inv);
        stmt.setInt(2, killId);
        stmt.executeUpdate();
    }
}
