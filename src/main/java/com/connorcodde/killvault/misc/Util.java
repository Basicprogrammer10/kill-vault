package com.connorcodde.killvault.misc;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static String inventoryToBase64(List<ItemStack> items) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

        dataOutput.writeInt(items.size());
        for (ItemStack item : items) dataOutput.writeObject(item);

        dataOutput.close();
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    public static List<ItemStack> fromBase64(String data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < dataInput.readInt(); i++) {
            items.add((ItemStack) dataInput.readObject());
        }
        dataInput.close();
        return items;
    }
}
