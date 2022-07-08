package com.connorcodde.killvault.gui;

import com.connorcodde.killvault.misc.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class GuiManager implements Listener {
    public static final Style BASE_STYLE = Style.style()
            .color(TextColor.color(NamedTextColor.GRAY))
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .build();
    public static final ItemStack BORDER_ITEM = Util.cleanItemStack(Material.RED_STAINED_GLASS_PANE, 1,
            m -> m.displayName(
                    Component.empty()));
    public static HashMap<UUID, Gui> inventory = new HashMap<>();

    public void open(UUID player, GuiInterface guiInterface, Inventory inventory) {
        Gui gui = new Gui(guiInterface, inventory);
        try {
            gui.inventory = gui.gui.open(getServer().getPlayer(player), gui.inventory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GuiManager.inventory.put(player, gui);
        Objects.requireNonNull(getServer().getPlayer(player))
                .openInventory(gui.inventory);
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent e) {
        inventory.remove(e.getPlayer()
                .getUniqueId());
    }

    @EventHandler
    void onInventoryClose(InventoryCloseEvent e) {
        if (!inventory.containsKey(e.getPlayer()
                .getUniqueId())) return;
        try {
            inventory.get(e.getPlayer()
                    .getUniqueId()).gui.close(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        inventory.remove(e.getPlayer()
                .getUniqueId());
    }

    @EventHandler
    void onInventoryClick(InventoryClickEvent e) {
        if (!inventory.containsKey(e.getWhoClicked()
                .getUniqueId())) return;
        try {
            inventory.get(e.getWhoClicked()
                    .getUniqueId()).gui.interact(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
