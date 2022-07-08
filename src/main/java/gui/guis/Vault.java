package gui.guis;

import com.connorcodde.killvault.misc.Util;
import gui.GuiInterface;
import gui.GuiManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import static gui.GuiManager.BASE_STYLE;
import static org.bukkit.Bukkit.getServer;

public class Vault implements GuiInterface {
    Inventory inventory;
    Player player;

    @Override
    public Inventory open(Player player) {
        inventory = getServer().createInventory(null, 9 * 5, Component.text("Your Vault"));
        this.player = player;

        for (int i = 0; i < 9 * 5; i++) {
            int mod = i % 9;
            int div = i / 9;
            if (mod != 0 && mod != 8 && div != 0 && div != 4) continue;
            inventory.setItem(i,
                    GuiManager.BORDER_ITEM);
        }
        inventory.setItem(40, Util.cleanItemStack(Material.HEART_OF_THE_SEA, 1,
                m -> m.displayName(Component.text("DELETE ALL", BASE_STYLE.color(
                        NamedTextColor.RED)))));
        return inventory;
    }

    @Override
    public void interact(InventoryClickEvent e) {

    }

    @Override
    public void close(InventoryCloseEvent e) {

    }
}
