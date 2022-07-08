package com.connorcodde.killvault.commands;

import com.connorcodde.killvault.KillVault;
import com.connorcodde.killvault.gui.GuiInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Vault implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        GuiInterface gui = new com.connorcodde.killvault.gui.guis.Vault();
        KillVault.guiManager.open(((Player) sender).getUniqueId(), gui, null);
        return true;
    }
}
