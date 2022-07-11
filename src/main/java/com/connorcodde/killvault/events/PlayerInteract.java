package com.connorcodde.killvault.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

import static org.bukkit.Bukkit.getServer;

public class PlayerInteract implements Listener {
    public static ArrayList<BlockDamageInfo> respawnAnchorExplosions = new ArrayList<>();

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || e.getClickedBlock()
                .getType() != Material.RESPAWN_ANCHOR || e.getClickedBlock()
                .getWorld()
                .getEnvironment() == World.Environment.NETHER || ((RespawnAnchor) e.getClickedBlock()
                .getBlockData()).getCharges() <= 0) {
            return;
        }

        respawnAnchorExplosions.add(new BlockDamageInfo(e.getPlayer(),
                e.getClickedBlock()
                        .getLocation(), getServer().getCurrentTick()));
    }

    public record BlockDamageInfo(Player player, Location location, int tick) {
        public boolean notCurrentTick() {
            return getServer().getCurrentTick() != tick;
        }
    }
}
