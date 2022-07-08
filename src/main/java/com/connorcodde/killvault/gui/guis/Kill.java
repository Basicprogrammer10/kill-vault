package com.connorcodde.killvault.gui.guis;

import com.connorcodde.killvault.gui.GuiInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class Kill implements GuiInterface {
    final int killId;

    public Kill(int killId) {
        this.killId = killId;
    }

    @Override
    public Inventory open(Player player, Inventory inv) {
        return null;
    }

    @Override
    public void interact(InventoryClickEvent e) {

    }

    @Override
    public void close(InventoryCloseEvent e) {

    }
}
