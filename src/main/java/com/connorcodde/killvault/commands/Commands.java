package com.connorcodde.killvault.commands;

import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class Commands {
    public static void init() {
        Objects.requireNonNull(getServer().getPluginCommand("vault"))
                .setExecutor(new Vault());
    }
}
