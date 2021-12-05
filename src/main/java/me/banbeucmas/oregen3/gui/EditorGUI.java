package me.banbeucmas.oregen3.gui;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.manager.ui.PlayerUI;
import me.banbeucmas.oregen3.manager.ui.chest.ChestUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EditorGUI extends ChestUI {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setName("§0").build();

    public EditorGUI(Player player) {
        super(player, "Edit Gui", 5);

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(2, 2, new ItemBuilder(XMaterial.FURNACE)
                .setName("§7Edit generators")
                .addLore("", "§7Click to edit generators")
                .build(), event -> {
            ListGenerator ui = new ListGenerator(player, 0);
            PlayerUI.openUI(player, ui);
        });
        set(8, 4, new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setSkull("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61")
                .setName("§aReload Plugin")
                .addLore("", "§7Click to reload config.yml", "")
                .build(), event -> {
            Oregen3.getPlugin().reload();
            EditorGUI ui = new EditorGUI(player);
            PlayerUI.openUI(player, ui);
        });
        for (int i = 0; i < 9; i++) set(i, 4, BORDER, null);
    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }
}
