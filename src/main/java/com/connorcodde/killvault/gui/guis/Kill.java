package com.connorcodde.killvault.gui.guis;

import com.connorcodde.killvault.KillVault;
import com.connorcodde.killvault.gui.GuiInterface;
import com.connorcodde.killvault.misc.Util;
import net.kyori.adventure.text.Component;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getOfflinePlayer;
import static org.bukkit.Bukkit.getServer;

public class Kill implements GuiInterface {
    final int killId;
    Inventory inventory;

    public Kill(int killId) {
        this.killId = killId;
    }

    @Override
    public Inventory open(Player player, Inventory inv) throws SQLException, IOException, ClassNotFoundException {
        inventory = inv;
        if (inventory == null || inventory.getSize() != 45)
            inventory = getServer().createInventory(null, 45, Component.text("Vault"));

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

        int index = 9;
        for (ItemStack i : diedInventory)
            inventory.setItem(index++, i);

        return inventory;
    }

    @Override
    public void interact(InventoryClickEvent e) {
        InventoryAction[] invalidActions = new InventoryAction[]{InventoryAction.CLONE_STACK, InventoryAction.HOTBAR_SWAP, InventoryAction.UNKNOWN};

        if (Arrays.stream(invalidActions)
                .anyMatch(d -> d == e.getAction()) || e.getSlot() == 8) e.setCancelled(true);
    }

    @Override
    public void close(InventoryCloseEvent e) {

    }
}
