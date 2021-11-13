package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.managers.items.ItemBuilder;
import me.banbeucmas.oregen3.managers.ui.PlayerUI;
import me.banbeucmas.oregen3.managers.ui.chest.ChestUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuGenerator extends ChestUI {

    public Generator generator;

    public MenuGenerator(Player player, ListGenerator listGenerator, Generator generator) {
        super(player, "Generator: %name".replace("%name", generator.getId()), 6);
        this.generator = generator;

        for (int i = 0; i < 9; i++) set(i, 0, ListGenerator.BORDER, null);
        set(0, 0, new ItemBuilder(XMaterial.ARROW.parseMaterial())
                .setName("§e <- Go Back ")
                .build(), event -> {
            PlayerUI.openUI(player, listGenerator);
        });
        for (int i = 0; i < 9; i++) set(i, 5, ListGenerator.BORDER, null);
    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }

}
