package com.connorcodde.killvault.gui.guis;

import com.connorcodde.killvault.gui.GuiInterface;
import com.connorcodde.killvault.gui.GuiManager;
import com.connorcodde.killvault.misc.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static org.bukkit.Bukkit.getServer;

public class Confirm implements GuiInterface {
    final String message;
    final Runnable yes;
    final Runnable no;
    Inventory inventory;
    int[] slots;

    Confirm(String message, Runnable yes, Runnable no) {
        this.message = message;
        this.yes = yes;
        this.no = no;
    }

    @Override
    public Inventory open(Player player, @Nullable Inventory inv) {
        inventory = inv;
        if (inventory == null || inventory.getSize() < 9 || inventory.getSize() % 9 != 0)
            inventory = getServer().createInventory(null, 45, Component.text("Vault"));

        int level = inventory.getSize() / 9 / 2 * 9 + 4;
        slots = new int[]{
                level - 2,
                level,
                level + 2
        };
        for (int i = 0; i < inventory.getSize(); i++) {
            int finalI = i;
            if (Arrays.stream(slots)
                    .anyMatch(d -> d == finalI)) continue;
            inventory.setItem(i, Util.cleanItemStack(Material.AIR, 1, m -> {
            }));
        }

        inventory.setItem(slots[0], Util.cleanItemStack(Material.RED_STAINED_GLASS_PANE, 1,
                m -> m.displayName(Component.text("Deny", GuiManager.BASE_STYLE.color(NamedTextColor.RED)))));

        inventory.setItem(slots[1], Util.cleanItemStack(Material.MAGENTA_STAINED_GLASS_PANE, 1,
                m -> m.displayName(Component.text(message, GuiManager.BASE_STYLE))));

        inventory.setItem(slots[2], Util.cleanItemStack(Material.LIME_STAINED_GLASS_PANE, 1,
                m -> m.displayName(Component.text("Confirm", GuiManager.BASE_STYLE.color(NamedTextColor.GREEN)))));

        return inventory;
    }

    @Override
    public void interact(InventoryClickEvent e) {
        e.setCancelled(true);

        if (e.getSlot() == slots[0]) no.run();
        if (e.getSlot() == slots[2]) yes.run();
    }

    @Override
    public void drag(InventoryDragEvent e) {

    }

    @Override
    public void close(InventoryCloseEvent e) {

    }
}
