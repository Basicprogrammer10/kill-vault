package com.connorcodde.killvault.events;

import com.connorcodde.killvault.KillVault;
import com.connorcodde.killvault.misc.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class PlayerDeath implements Listener {
    @EventHandler
    void onPlayerDeath(PlayerDeathEvent e) throws IOException, SQLException {
        if (e.getPlayer()
                .getKiller() == null) return;
        UUID killer = e.getPlayer()
                .getKiller()
                .getUniqueId();
        UUID died = e.getPlayer()
                .getUniqueId();
        String deathMessage = e.getDeathMessage();
        long deathTime = Instant.now()
                .getEpochSecond();
        String inventory = Util.inventoryToBase64(Arrays.asList(e.getPlayer()
                .getInventory()
                .getContents()));

        PreparedStatement stmt = KillVault.database.connection.prepareStatement(
                "INSERT INTO deaths (killer, dieer, deathInventory, deathMessage, deathTime) VALUES (?, ?, ?, ?, ?)");
        stmt.setString(1, killer.toString());
        stmt.setString(2, died.toString());
        stmt.setString(3, inventory);
        stmt.setString(4, deathMessage);
        stmt.setLong(5, deathTime);
        stmt.executeUpdate();

        PreparedStatement stmt2 = KillVault.database.connection.prepareStatement(
                "SELECT id FROM deaths WHERE killer = ?1 ORDER BY deaths.deathTime DESC LIMIT max((SELECT COUNT(*) FROM deaths WHERE killer = ?1) - 21, 0);");
        stmt2.setString(1, killer.toString());
        ResultSet res = stmt2.executeQuery();

        KillVault.database.connection.setAutoCommit(false);
        PreparedStatement stmt3 = KillVault.database.connection.prepareStatement("DELETE FROM deaths WHERE id = ?");
        while (res.next()) {
            stmt3.setInt(1, res.getInt(1));
            stmt3.executeUpdate();
        }
        KillVault.database.connection.commit();
        KillVault.database.connection.setAutoCommit(true);
    }
}
