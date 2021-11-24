package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.managers.items.ItemBuilder;
import me.banbeucmas.oregen3.managers.items.SkullIndex;
import me.banbeucmas.oregen3.managers.ui.PlayerUI;
import me.banbeucmas.oregen3.managers.ui.chest.ChestUI;
import me.banbeucmas.oregen3.utils.StringUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
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
        super(player, "Generators [p.%page]".replace("%page", String.valueOf(page + 1)), 6);
        this.page = page;

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(0, 0, new ItemBuilder(XMaterial.ARROW.parseMaterial())
                .setName("§e <- Go Back ")
                .build(), event -> {
            PlayerUI.openUI(player, editorGUI);
        });
        renderPage();
        for (int i = 0; i < 9; i++) set(i, 5, BORDER, null);
    }

    private void renderPage() {
        Map<String, Generator> map = DataManager.getChoosers();
        List<Generator> choosers = new ArrayList<>(map.values());
        
        if (page > 0) set(2, 0, new ItemBuilder(SkullIndex.PREVIOUS).setName("§e <- Previous Page ").build(), event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page--;
            renderPage();
        });
        if ((page + 1) * 36 < choosers.size()) set(6, 0, new ItemBuilder(SkullIndex.NEXT).setName("§e Next Page -> ").build(), event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
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

            Configuration config = Oregen3.getPlugin().getConfig();
            ConfigurationSection path = config.getConfigurationSection("generators." + info.getId() + ".random");
            List<String> materials = new ArrayList<>(path.getKeys(true));
            ItemStack item = XMaterial.COBBLESTONE.parseItem();

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§7Generator §6" + info.getId());
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Name: " + info.getName());
            lore.add("§7Permission: " + info.getPermission());
            lore.add("§7Priority: " + info.getPriority());
            lore.add("§7Level: " + info.getLevel());
            lore.add("");
            lore.add("§7Random:");
            for (int mc = 0; mc < materials.size(); mc++){
                lore.add("§6 ● §8" + materials.get(mc) + ":§e " + StringUtils.DOUBLE_FORMAT.format(config.getDouble("generators." + info.getId() + ".random." + materials.get(mc))));
            }
            lore.add("");
            lore.add("§eClick to edit.");
            meta.setLore(lore);
            item.setItemMeta(meta);

            set(i % 9, 1 + (i / 9), item, event -> {
                MenuGenerator ui = new MenuGenerator(player, this, info);
                PlayerUI.openUI(player, ui);
            });
        }
    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }

}
