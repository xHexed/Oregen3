package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.manager.ui.PlayerUI;
import me.banbeucmas.oregen3.manager.ui.chest.ChestUI;
import me.banbeucmas.oregen3.util.StringUtils;
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

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setName("§0").build();
    protected static final ItemStack NEXT = new ItemBuilder(XMaterial.PLAYER_HEAD).setName("§e Next Page -> ").setSkull("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").build();
    protected static final ItemStack PREVIOUS = new ItemBuilder(XMaterial.PLAYER_HEAD).setName("§e <- Previous Page ").setSkull("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build();

    private int page;

    public ListGenerator(Player player, int page) {
        super(player, "Generators", 6);
        this.page = page;

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(0, 0, new ItemBuilder(XMaterial.ARROW)
                .setName("§e <- Go Back ")
                .build(), event -> {
            EditorGUI ui = new EditorGUI(player);
            PlayerUI.openUI(player, ui);
        });
        renderPage();
        for (int i = 0; i < 9; i++) set(i, 5, BORDER, null);
    }

    private void renderPage() {
        Map<String, Generator> map = DataManager.getChoosers();
        List<Generator> choosers = new ArrayList<>(map.values());

        if (page > 0) { set(2, 0, PREVIOUS, event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page--;
            renderPage();
        });} else set(2, 0, BORDER, null);
        if ((page + 1) * 36 < choosers.size()) { set(6, 0, NEXT, event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page++;
            renderPage();
        });} else set(6, 0, BORDER, null);
        
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
            if (materials.size() > 0) lore.add("§7Random:");
            for (int mc = 0; mc < materials.size(); mc++){
                lore.add("§6 ● §8" + materials.get(mc) + ":§e " + StringUtils.DOUBLE_FORMAT.format(config.getDouble("generators." + info.getId() + ".random." + materials.get(mc))));
                if (mc == 5) {
                    int mleft = materials.size() - mc;
                    lore.add("§6 ● §8And §6" + mleft + "§8 more...");
                    break;
                }
            }
            if (materials.size() > 0)lore.add("");
            lore.add("§eClick to edit.");
            meta.setLore(lore);
            item.setItemMeta(meta);

            set(i % 9, 1 + (i / 9), item, event -> {
                MenuGenerator ui = new MenuGenerator(player, info);
                PlayerUI.openUI(player, ui);
            });
        }
    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }

}
