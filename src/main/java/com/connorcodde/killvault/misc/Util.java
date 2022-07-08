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
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static ItemStack cleanItemStack(Material material, int count, ItemMetaEditor itemMetaEditor) {
        ItemStack item = new ItemStack(material, count);
        ItemMeta itemMeta = item.getItemMeta();
        itemMetaEditor.run(itemMeta);

        item.setItemMeta(itemMeta);
        return item;
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
