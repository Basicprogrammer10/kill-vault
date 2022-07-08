package com.connorcodde.killvault.misc;

import com.connorcodde.killvault.KillVault;
import com.connorcodde.killvault.gui.GuiManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;

import static com.connorcodde.killvault.gui.guis.Vault.ID_NAMESPACE;
import static org.bukkit.Bukkit.getServer;

public class Util {
    public static final Map<String, Integer> timeUnits = new LinkedHashMap<>();

    static {
        timeUnits.put("second", 60);
        timeUnits.put("minute", 60);
        timeUnits.put("hour", 24);
        timeUnits.put("day", 0);
    }

    public static ItemStack cleanItemStack(Material material, int count, ItemMetaEditor itemMetaEditor) {
        ItemStack item = new ItemStack(material, count);
        ItemMeta itemMeta = item.getItemMeta();
        itemMetaEditor.run(itemMeta);

        item.setItemMeta(itemMeta);
        return item;
    }

    public static String formatEpochTime(long epochSeconds) {
        float diff = Instant.now()
                .getEpochSecond() - epochSeconds;

        for (Map.Entry<String, Integer> i : timeUnits.entrySet()) {
            if (i.getValue() == 0 || diff < i.getValue()) {
                diff = Math.round(diff);
                return String.format("%d %s%s ago", (int) diff, i.getKey(), diff > 1 ? "s" : "");
            }
            diff /= i.getValue();
        }
        return String.format("%d days", Math.round(diff));
    }

    public static void setPlayerItem(Inventory inv, int index, OfflinePlayer player, String deathMessage, long deathTime, int id) {
        inv.setItem(index, Util.cleanItemStack(Material.PLAYER_HEAD, 1, m -> {
            m.displayName(Component.text(player.getName() == null ? "UNKNOWN" : player.getName(),
                    GuiManager.BASE_STYLE.color(NamedTextColor.YELLOW)));
            ((SkullMeta) m).setOwningPlayer(player);

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(deathMessage, GuiManager.BASE_STYLE));
            lore.add(Component.text(Util.formatEpochTime(deathTime), GuiManager.BASE_STYLE));
            m.lore(lore);

            if (id < 0) return;
            m.getPersistentDataContainer()
                    .set(ID_NAMESPACE, PersistentDataType.INTEGER, id);
        }));
    }

    public static String inventoryToBase64(List<ItemStack> items) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

        dataOutput.writeInt(items.size());
        for (ItemStack item : items) dataOutput.writeObject(item);

        dataOutput.close();
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    public static List<ItemStack> inventoryFromBase64(String data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        List<ItemStack> items = new ArrayList<>();

        int len = dataInput.readInt();
        for (int i = 0; i < len; i++)
            items.add((ItemStack) dataInput.readObject());
        dataInput.close();
        return items;
    }

    public static void checkVersion() {
        try {
            URL url = new URL("https://version.connorcode.com/KillVault/status?code=xLF5EH6pUnTUnDPcb9Bg");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "text/plain");
            InputStream responseStream = connection.getInputStream();

            Scanner scanner = new Scanner(responseStream, StandardCharsets.UTF_8.name());
            String version = scanner.useDelimiter("\\A")
                    .next()
                    .split(",")[0];

            if (version.equals(KillVault.VERSION)) return;
            getServer().getLogger()
                    .log(Level.WARNING, String.format("Version Outdated! (%s > %s)", version, KillVault.VERSION));
        } catch (IOException ignored) {
        }
    }
}
