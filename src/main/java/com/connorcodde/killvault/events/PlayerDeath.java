package com.connorcodde.killvault.events;

import com.connorcodde.killvault.KillVault;
import com.connorcodde.killvault.misc.Util;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.connorcodde.killvault.events.PlayerInteract.respawnAnchorExplosions;

public class PlayerDeath implements Listener {
    @EventHandler
    void onPlayerDeath(PlayerDeathEvent e) throws IOException, SQLException {
        if (Arrays.stream(e.getPlayer()
                        .getInventory()
                        .getContents())
                .filter(Objects::nonNull)
                .allMatch(i -> i.isSimilar(new ItemStack(
                        Material.AIR)))) return;

        Optional<Player> findKiller = findKiller(Objects.requireNonNull(e.getPlayer()
                        .getLastDamageCause())
                .getEntity());
        if (findKiller.isEmpty() && e.getPlayer()
                .getLastDamageCause()
                .getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            for (PlayerInteract.BlockDamageInfo i : respawnAnchorExplosions) {
                if (i.notCurrentTick() || e.getPlayer()
                        .getLocation()
                        .distance(i.location()) > 5) continue;
                findKiller = Optional.of(i.player());
                break;
            }
        }
        respawnAnchorExplosions.removeIf(PlayerInteract.BlockDamageInfo::notCurrentTick);

        if (findKiller.isEmpty()) return;
        Player killer = findKiller.get();

        String deathMessage = PlainTextComponentSerializer.plainText()
                .serialize(Objects.requireNonNull(e.deathMessage()));
        UUID killerUUID = killer.getUniqueId();
        UUID diedUUID = e.getPlayer()
                .getUniqueId();
        long deathTime = Instant.now()
                .getEpochSecond();
        String inventory = Util.inventoryToBase64(Arrays.asList(e.getPlayer()
                .getInventory()
                .getContents()));

        PreparedStatement stmt = KillVault.database.connection.prepareStatement(
                "INSERT INTO deaths (killer, dieer, deathInventory, headRemoved, deathMessage, deathTime) VALUES (?, ?, ?, 0, ?, ?)");
        stmt.setString(1, killerUUID.toString());
        stmt.setString(2, diedUUID.toString());
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

        e.getDrops()
                .clear();
    }

    // From https://github.com/plan-player-analytics/Plan
    public Optional<Player> findKiller(Entity dead) {
        EntityDamageEvent entityDamageEvent = dead.getLastDamageCause();
        if (!(entityDamageEvent instanceof EntityDamageByEntityEvent)) return Optional.empty();

        Entity killer = ((EntityDamageByEntityEvent) entityDamageEvent).getDamager();
        if (killer instanceof Player) return Optional.of((Player) killer);
        if (killer instanceof Tameable) return getOwner((Tameable) killer);
        if (killer instanceof Projectile) return getShooter((Projectile) killer);
        if (killer instanceof EnderCrystal) return findKiller(killer);
        return Optional.empty();
    }

    private Optional<Player> getShooter(Projectile projectile) {
        ProjectileSource source = projectile.getShooter();
        if (source instanceof Player) return Optional.of((Player) source);
        return Optional.empty();
    }

    private Optional<Player> getOwner(Tameable tameable) {
        if (!tameable.isTamed()) return Optional.empty();

        AnimalTamer owner = tameable.getOwner();
        if (owner instanceof Player) return Optional.of((Player) owner);
        return Optional.empty();
    }
}
