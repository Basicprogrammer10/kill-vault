package gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;

public interface GuiInterface {
    Inventory open(Player player) throws SQLException;

    void interact(InventoryClickEvent e);

    void close(InventoryCloseEvent e);
}
