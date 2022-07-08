package com.connorcodde.killvault.events;

import com.connorcodde.killvault.KillVault;
import org.bukkit.plugin.PluginManager;

import static org.bukkit.Bukkit.getServer;

public class Events {
    public static void init() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerDeath(), KillVault.plugin);
        pm.registerEvents(KillVault.guiManager, KillVault.plugin);
    }
}
