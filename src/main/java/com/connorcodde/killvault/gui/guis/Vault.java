package com.connorcodde.killvault.gui.guis;

import com.connorcodde.killvault.KillVault;
import com.connorcodde.killvault.gui.Gui;
import com.connorcodde.killvault.gui.GuiManager;
import com.connorcodde.killvault.misc.Util;
import com.connorcodde.killvault.gui.GuiInterface;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getOfflinePlayer;
import static org.bukkit.Bukkit.getServer;

public class Vault implements GuiInterface {
    static final NamespacedKey ID_NAMESPACE = new NamespacedKey(KillVault.plugin, "id");
    Inventory inventory;
    Player player;

    @Override
    public Inventory open(Player player, Inventory inv) throws SQLException {
        inventory = inv;
        this.player = player;
        if (inventory == null || inventory.getSize() != 45)
            inventory = getServer().createInventory(null, 45, Component.text("Vault"));

        // Draw Border
        for (int i = 0; i < 9 * 5; i++) {
            int mod = i % 9;
            int div = i / 9;
            if (mod != 0 && mod != 8 && div != 0 && div != 4) continue;
            inventory.setItem(i, GuiManager.BORDER_ITEM);
        }
        inventory.setItem(40, Util.cleanItemStack(Material.HEART_OF_THE_SEA, 1,
                m -> m.displayName(Component.text("DELETE ALL", GuiManager.BASE_STYLE.color(NamedTextColor.RED)))));

        // Query Database

        PreparedStatement stmt = KillVault.database.connection.prepareStatement(
                "SELECT id, dieer, deathMessage, deathTime FROM deaths WHERE killer = ? ORDER BY deathTime DESC");
        stmt.setString(1, player.getUniqueId()
                .toString());
        ResultSet res = stmt.executeQuery();

        // Add heads
        for (int i = 0; i < 21; i++) {
            int index = 10 + (i / 7 * 9) + (i % 7);

            if (!res.next()) {
                inventory.setItem(index, Util.cleanItemStack(Material.AIR, 1, itemMeta -> {
                }));
                continue;
            }

            int id = res.getInt(1);
            UUID dead = UUID.fromString(res.getString(2));
            OfflinePlayer deadPlayer = getOfflinePlayer(dead);
            String deathMessage = res.getString(3);
            int deathTime = res.getInt(4);

            inventory.setItem(index, Util.cleanItemStack(Material.PLAYER_HEAD, 1, m -> {
                m.displayName(Component.text(deadPlayer.getName() == null ? "UNKNOWN" : deadPlayer.getName(),
                        GuiManager.BASE_STYLE.color(NamedTextColor.YELLOW)));
                ((SkullMeta) m).setOwningPlayer(deadPlayer);

                List<Component> lore = new ArrayList<>();
                lore.add(Component.text(deathMessage, GuiManager.BASE_STYLE));
                lore.add(Component.text(Util.formatEpochTime(deathTime), GuiManager.BASE_STYLE));
                m.lore(lore);
                m.getPersistentDataContainer()
                        .set(ID_NAMESPACE, PersistentDataType.INTEGER, id);
            }));
        }

        return inventory;
    }

    @Override
    public void interact(InventoryClickEvent e) throws Exception {
        // Cancel Event
        e.setCancelled(true);

        // Handle Delete all button
        if (e.getSlot() == 40) {
            deleteAll();
            return;
        }

        if (Objects.requireNonNull(e.getClickedInventory())
                .getStorageContents()[e.getSlot()].getType() != Material.PLAYER_HEAD) return;
        if (e.isLeftClick()) openKill(e);
        if (e.isRightClick()) deleteOne(e);
    }

    @Override
    public void close(InventoryCloseEvent e) {

    }

    void deleteAll() throws Exception {
        // Define actions on yes / no
        Runnable no = () -> {
            Gui gui2 = new Gui(this, inventory);
            try {
                gui2.inventory = gui2.gui.open(player, inventory);
            } catch (Exception e) {
                e.printStackTrace();
            }
            GuiManager.inventory.put(player.getUniqueId(), gui2);
        };

        Runnable yes = () -> {
            try {
                PreparedStatement stmt = KillVault.database.connection.prepareStatement(
                        "DELETE FROM deaths WHERE killer = ?");
                stmt.setString(1, player.getUniqueId()
                        .toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            no.run();
        };

        // Open confirm dialog
        GuiInterface gui = new Confirm("Are you sure you want to delete all your kills?", yes, no);

        gui.open(player, inventory);
        GuiManager.inventory.put(player.getUniqueId(), new Gui(gui, inventory));
    }

    void deleteOne(InventoryClickEvent e) throws SQLException {
        int id = Objects.requireNonNull(Objects.requireNonNull(e.getClickedInventory())
                .getStorageContents()[e.getSlot()].getItemMeta()
                .getPersistentDataContainer()
                .get(ID_NAMESPACE, PersistentDataType.INTEGER));

        // Remove kill from vault
        PreparedStatement stmt = KillVault.database.connection.prepareStatement(
                "DELETE FROM deaths WHERE killer = ? AND id = ?");
        stmt.setString(1, player.getUniqueId()
                .toString());
        stmt.setInt(2, id);
        stmt.executeUpdate();

        // Refresh vault
        open(player, inventory);
    }

    void openKill(InventoryClickEvent e) throws Exception {
        GuiInterface gui = new Kill(Objects.requireNonNull(e.getInventory()
                .getStorageContents()[e.getSlot()].getItemMeta()
                .getPersistentDataContainer()
                .get(ID_NAMESPACE, PersistentDataType.INTEGER)));
        gui.open(player, inventory);
        GuiManager.inventory.put(player.getUniqueId(), new Gui(gui, inventory));
    }
}
