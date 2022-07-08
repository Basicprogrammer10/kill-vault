package gui;

import com.connorcodde.killvault.KillVault;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public interface GuiInterface {
    Inventory open(Player player);

    void interact(InventoryClickEvent e);

    void close(InventoryCloseEvent e);
}
