package com.connorcodde.killvault;

import com.connorcodde.killvault.commands.Commands;
import com.connorcodde.killvault.events.Events;
import gui.GuiManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class KillVault extends JavaPlugin {
    public static JavaPlugin plugin;
    public static GuiManager guiManager = new GuiManager();

    @Override
    public void onEnable() {
        plugin = getPlugin(KillVault.class);

        // Init events and commands
        Events.init();
        Commands.init();
    }

    @Override
    public void onDisable() {

    }
}
