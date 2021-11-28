package me.banbeucmas.oregen3.gui.editor.options;

import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.manager.ui.chest.ChestUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CreateRandomOraxen extends ChestUI {

    public CreateRandomOraxen(Player player/**, CreateRandomBlock createRandomBlock, Generator generator*/, int page) {
        super(player, "Choose Oraxen Block You Want", 6);
    }
    
    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }
}
