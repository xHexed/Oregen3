package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.SlotIterator;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryContents;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.util.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ListGenerator {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()).setName("§0").build();

    public static void open(Player player) {
        RyseInventory listGenerator = RyseInventory.builder()
                .title("Generators")
                .rows(6)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        Pagination pagination = contents.pagination();
                        pagination.setItemsPerPage(36);
                        pagination.iterator(SlotIterator.builder().startPosition(1, 0).type(SlotIterator.SlotIteratorType.HORIZONTAL).build());

                        for (int i = 0; i < 9; i++) contents.set(i, BORDER);
                        contents.set(0, IntelligentItem.of(new ItemBuilder(XMaterial.ARROW.parseMaterial())
                                .setName("§e <- Go Back ")
                                .build(), event -> EditorGUI.open(player)));
                        for (int i = 0; i < 9; i++) contents.set(5, i, BORDER);

                        contents.set(5, 2, IntelligentItem.of(new ItemBuilder(Material.ARROW).setAmount((pagination.isFirst() ? 1 : pagination.page() - 1)).setName("§e <- Previous Page ").build(), event -> {
                            if (pagination.isFirst()) {
                                return;
                            }

                            RyseInventory currentInventory = pagination.inventory();
                            currentInventory.open(player, pagination.previous().page());
                        }));

                        contents.set(5, 6, IntelligentItem.of(new ItemBuilder(Material.ARROW).setName("§e Next Page -> ").build(), event -> {
                            if (pagination.isLast()) {
                                return;
                            }

                            RyseInventory currentInventory = pagination.inventory();
                            currentInventory.open(player, pagination.next().page());
                        }));

                        Map<String, Generator> map = DataManager.getChoosers();
                        List<Generator> choosers = new ArrayList<>(map.values());

                        for (Generator info : choosers) {
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
                            for (int mc = 0; mc < materials.size(); mc++) {
                                lore.add("§6 ● §8" + materials.get(mc) + ":§e " + StringUtils.DOUBLE_FORMAT.format(config.getDouble("generators." + info.getId() + ".random." + materials.get(mc))) + "%");
                            }
                            lore.add("");
                            lore.add("§eClick to edit.");
                            meta.setLore(lore);
                            item.setItemMeta(meta);

                            pagination.addItem(IntelligentItem.of(item, event -> {
                                MenuGenerator.open(player, info);
                            }));
                        }
                    }
                })
                .build(Oregen3.getPlugin());
        listGenerator.open(player);
    }

}
