package com.connorcodde.killvault.gui;

import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class Gui implements Listener {
    public final GuiInterface gui;
    public Inventory inventory;

    public Gui(GuiInterface guiInterface, Inventory inventory) {
        this.gui = guiInterface;
        this.inventory = inventory;
    }
}
