package me.banbeucmas.oregen3.gui.editor.options;

import com.cryptomorin.xseries.XMaterial;
import io.th0rgal.oraxen.items.OraxenItems;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.editor.MenuGenerator;
import me.banbeucmas.oregen3.managers.items.ItemBuilder;
import me.banbeucmas.oregen3.managers.ui.PlayerUI;
import me.banbeucmas.oregen3.managers.ui.chest.ChestUI;
import me.banbeucmas.oregen3.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ListRandomBlock extends ChestUI {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).setName("§0").build();

    public Generator generator;
    private int page;

    public ListRandomBlock(Player player, MenuGenerator menuGenerator, Generator generator, int page) {
        super(player, "Edit random blocks (%name) [p.%page]"
                .replace("%name", generator.getId())
                .replace("%page", String.valueOf(page + 1)), 6);
        this.generator = generator;
        this.page = page;

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(0, 0, new ItemBuilder(XMaterial.ARROW.parseMaterial())
                .setName("§e <- Go Back ")
                .build(), event -> {
            PlayerUI.openUI(player, menuGenerator);
        });
        renderPage();
        for (int i = 0; i < 9; i++) set(i, 5, BORDER, null);
    }

    private void renderPage() {

        Configuration config = Oregen3.getPlugin().getConfig();
        ConfigurationSection path = config.getConfigurationSection("generators." + generator.getId() + ".random");
        List<String> materials = new ArrayList<>(path.getKeys(false));

        for (int i = 0; i < 36; i++) {

            int matIndex = page * 36 + i;
            if (materials.size() <= matIndex) {
                clearSlot(i % 9, 1 + (i / 9));
                continue;
            }

            String material = materials.get(matIndex);

            if (material.startsWith("oraxen-")) {
                if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
                    ItemStack item = OraxenItems.getItemById(material.substring(7)).build();
                    ItemMeta meta = item.getItemMeta();
                    item.setItemMeta(meta);

                    set(i % 9, 1 + (i / 9), item, null);
                } else {
                    set(i % 9, 1 + (i / 9), new ItemBuilder(XMaterial.BEDROCK.parseMaterial()).build(), null);
                }
            } else {
                ItemStack item = XMaterial.matchXMaterial(material).get().parseItem();
                ItemMeta meta = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add("§7Chances: " + StringUtils.DOUBLE_FORMAT.format(config.getDouble("generators." + generator.getId() + ".random." + material)));
                lore.add("");
                lore.add("§8[§2Left-Click§8]§e to edit chances");
                lore.add("§8[§2Right-Click§8]§e to delete");
                meta.setLore(lore);
                item.setItemMeta(meta);

                set(i % 9, 1 + (i / 9), item, null);
            }
        }

    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }
}
