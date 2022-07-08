package com.connorcodde.killvault.events;

import com.connorcodde.killvault.KillVault;

import static org.bukkit.Bukkit.getServer;

public class Events {
    public static void init() {
        getServer().getPluginManager()
                .registerEvents(new PlayerDeath(), KillVault.plugin);
    }
}
