package gui.guis;

import gui.GuiInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class Kill implements GuiInterface {
    @Override
    public Inventory open(Player player, Inventory inv) {
        return null;
    }

    @Override
    public void interact(InventoryClickEvent e) {

    }

    @Override
    public void close(InventoryCloseEvent e) {

    }
}
