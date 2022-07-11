package com.connorcodde.killvault.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

public interface GuiInterface {
    Inventory open(Player player, @Nullable Inventory inventory) throws Exception;

    void interact(InventoryClickEvent e) throws Exception;

    void drag(InventoryDragEvent e) throws Exception;

    void close(InventoryCloseEvent e) throws Exception;
}
