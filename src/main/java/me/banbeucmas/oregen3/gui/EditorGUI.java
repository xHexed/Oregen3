package me.banbeucmas.oregen3.gui;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.managers.items.ItemBuilder;
import me.banbeucmas.oregen3.managers.ui.PlayerUI;
import me.banbeucmas.oregen3.managers.ui.chest.ChestUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EditorGUI extends ChestUI {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).setName("§0").build();

    public EditorGUI(Player player) {
        super(player, "Edit Gui", 5);

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(3, 2, new ItemBuilder(XMaterial.FURNACE.parseMaterial())
                .setName("§7Edit generators")
                .addLore("", "§7Click to edit generators")
                .build(), event -> {
            ListGenerator ui = new ListGenerator(player, this, 0);
            PlayerUI.openUI(player, ui);
        });
        for (int i = 0; i < 9; i++) set(i, 4, BORDER, null);
    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }
}
