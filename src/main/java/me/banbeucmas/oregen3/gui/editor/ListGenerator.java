package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.managers.items.ItemBuilder;
import me.banbeucmas.oregen3.managers.items.SkullIndex;
import me.banbeucmas.oregen3.managers.ui.PlayerUI;
import me.banbeucmas.oregen3.managers.ui.chest.ChestUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListGenerator extends ChestUI {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).setName("§0").build();

    private int page;

    public ListGenerator(Player player, EditorGUI editorGUI, int page) {
        super(player, "Generators [p.%page]".replace("%page", String.valueOf(page)), 6);
        this.page = page;

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(1, 0, new ItemBuilder(XMaterial.ARROW.parseMaterial())
                .setName("§e <- Go Back ")
                .build(), event -> {
            PlayerUI.openUI(player, editorGUI);
        });
        renderPage();
    }

    private void renderPage() {
        final List<String, Generator> choosers = DataManager.getChoosers();
        
        if (page > 0) set(2, 0, new ItemBuilder(SkullIndex.PREVIOUS).setName("§e <- Previous Page ").build(), event -> {
            page--;
            renderPage();
        });
        if ((page + 1) * 36 < choosers.size()) set(7, 0, new ItemBuilder(SkullIndex.NEXT).setName("§e Next Page -> ").build(), event -> {
            page++;
            renderPage();
        });
        
        for (int i = 0; i < 36; i++) {
            int genIndex = page * 36 + i;
            if (choosers.size() <= genIndex) {
                clearSlot(i % 9, 1 + (i / 9));
                continue;
            }

            Generator info = choosers.get(genIndex);
            ItemStack item = XMaterial.COBBLESTONE.parseItem();

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(info.getId());
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Permission: " + info.getPermission());
            lore.add("§7Priority: " + info.getPriority());
            lore.add("§7Level: " + info.getLevel());
            lore.add("");

            set(i % 9, 1 + (i / 9), item, null);
        }
    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }

}
