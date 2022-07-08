package com.connorcodde.killvault;

import com.connorcodde.killvault.events.Events;
import org.bukkit.plugin.java.JavaPlugin;

public final class KillVault extends JavaPlugin {
    public static JavaPlugin plugin = getPlugin(KillVault.class);

    @Override
    public void onEnable() {
        // Init events
        Events.init();
    }

    @Override
    public void onDisable() {

    }
}
