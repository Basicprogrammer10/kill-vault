package gui;

import gui.guis.Kill;
import gui.guis.Vault;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class Gui implements Listener {
    public final GuiInterface gui;
    public final Inventory inventory;

    public Gui(Player player, GuiType type) {
        gui = switch (type) {
            case BaseVault -> new Vault();
            case Kill -> new Kill();
        };
        inventory = gui.open(player);
    }
}
