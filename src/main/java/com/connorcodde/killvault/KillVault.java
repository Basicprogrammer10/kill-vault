package com.connorcodde.killvault;

import com.connorcodde.killvault.commands.Commands;
import com.connorcodde.killvault.events.Events;
import com.connorcodde.killvault.misc.Database;
import com.connorcodde.killvault.gui.GuiManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class KillVault extends JavaPlugin {
    public static JavaPlugin plugin;
    public static Database database;
    public static GuiManager guiManager = new GuiManager();

    @Override
    public void onEnable() {
        plugin = getPlugin(KillVault.class);

        // Init Database
        database = new Database("data.db");

        // Init events and commands
        Events.init();
        Commands.init();
    }

    @Override
    public void onDisable() {
        try {
            database.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
