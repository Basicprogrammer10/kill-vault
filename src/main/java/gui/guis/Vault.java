package gui.guis;

import com.connorcodde.killvault.KillVault;
import com.connorcodde.killvault.misc.Util;
import gui.GuiInterface;
import gui.GuiManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static gui.GuiManager.BASE_STYLE;
import static org.bukkit.Bukkit.getOfflinePlayer;
import static org.bukkit.Bukkit.getServer;

public class Vault implements GuiInterface {
    Inventory inventory;
    Player player;

    @Override
    public Inventory open(Player player, Inventory inv) throws SQLException {
        inventory = inv;
        this.player = player;
        if (inv == null || inv.getSize() != 45)
            inventory = getServer().createInventory(null, 45, Component.text("Vault"));

        // Draw Border
        for (int i = 0; i < 9 * 5; i++) {
            int mod = i % 9;
            int div = i / 9;
            if (mod != 0 && mod != 8 && div != 0 && div != 4) continue;
            inventory.setItem(i, GuiManager.BORDER_ITEM);
        }
        inventory.setItem(40, Util.cleanItemStack(Material.HEART_OF_THE_SEA, 1,
                m -> m.displayName(Component.text("DELETE ALL", BASE_STYLE.color(NamedTextColor.RED)))));

        // Query Database

        PreparedStatement stmt = KillVault.database.connection.prepareStatement(
                "SELECT dieer, deathMessage, deathTime FROM deaths WHERE killer = ?");
        stmt.setString(1, player.getUniqueId()
                .toString());
        ResultSet res = stmt.executeQuery();

        // Add heads
        for (int i = 0; i < 21; i++) {
            int index = 10 + (i / 7 * 9) + (i % 7);

            if (!res.next()) {
                inventory.setItem(index, Util.cleanItemStack(Material.AIR, 1, itemMeta -> {
                }));
                continue;
            }

            UUID dead = UUID.fromString(res.getString(1));
            OfflinePlayer deadPlayer = getOfflinePlayer(dead);
            String deathMessage = res.getString(2);
            int deathTime = res.getInt(3);

            inventory.setItem(index, Util.cleanItemStack(Material.PLAYER_HEAD, 1, m -> {
                m.displayName(Component.text(Objects.requireNonNull(deadPlayer.getName()),
                        BASE_STYLE.color(NamedTextColor.YELLOW)));
                ((SkullMeta) m).setOwningPlayer(deadPlayer);

                List<Component> lore = new ArrayList<>();
                lore.add(Component.text(deathMessage, BASE_STYLE));
                lore.add(Component.text(LocalDateTime.ofEpochSecond(deathTime, 0, ZoneOffset.UTC)
                        .format(DateTimeFormatter.ofPattern("HH:mm:ss - MM/dd/uuuu")), BASE_STYLE));
                m.lore(lore);
            }));
        }

        return inventory;
    }

    @Override
    public void interact(InventoryClickEvent e) {
        if (e.getAction() == InventoryAction.NOTHING || e.getClickedInventory() != inventory) return;
        e.setCancelled(true);
    }

    @Override
    public void close(InventoryCloseEvent e) {

    }

    void refresh(Inventory inventory) {

    }

    void deleteAll() {
    }
}
