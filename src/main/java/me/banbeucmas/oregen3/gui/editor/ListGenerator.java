package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.util.StringUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ListGenerator {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("§0").build();

    public static void open(Player player) {
        RyseInventory listGenerator = RyseInventory.builder()
                .identifier("ListGenerator")
                .title("Generators [p.1]")
                .rows(6)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        Pagination pagination = contents.pagination();

                        pagination.setItemsPerPage(36);
                        pagination.iterator(SlotIterator.builder().startPosition(1, 0).type(SlotIterator.SlotIteratorType.HORIZONTAL).build());

                        contents.fillRow(0, BORDER);
                        contents.set(0, IntelligentItem.of(new ItemBuilder(XMaterial.ARROW.parseMaterial())
                                .setName("§e <- Go Back ")
                                .build(), event -> EditorGUI.open(player)));
                        contents.fillRow(45, BORDER);

                        movePage(player, contents, pagination);

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
                            listRandom(config, materials, lore, info);
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

    public static void movePage(Player player, InventoryContents contents, Pagination pagination) {
        contents.set(5, 2, IntelligentItem.of(new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                .setName("§e <- Previous Page ")
                .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQxMzNmNmFjM2JlMmUyNDk5YTc4NGVmYWRjZmZmZWI5YWNlMDI1YzM2NDZhZGE2N2YzNDE0ZTVlZjMzOTQifX19")
                .build(), event -> {
            if (pagination.isFirst()) {
                return;
            }

            RyseInventory currentInventory = pagination.inventory();
            currentInventory.open(player, pagination.previous().page());
        }));

        contents.set(5, 6, IntelligentItem.of(new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                .setName("§e Next Page -> ")
                .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTAyZmEzYjJkY2IxMWM2NjM5Y2M5YjkxNDZiZWE1NGZiYzY2NDZkODU1YmRkZTFkYzY0MzUxMjRhMTEyMTVkIn19fQ==")
                .build(), event -> {
            if (pagination.isLast()) {
                return;
            }

            RyseInventory currentInventory = pagination.inventory();
            currentInventory.open(player, pagination.next().page());
        }));
    }

    public static void listRandom(Configuration config, List<String> materials, List<String> lore, Generator generator) {
        for (int mc = 0; mc < materials.size(); mc++) {
            if (mc < 10) lore.add("§6 ● §8" + materials.get(mc) + ":§e " + StringUtils.DOUBLE_FORMAT.format(config.getDouble("generators." + generator.getId() + ".random." + materials.get(mc))) + "%");
        }
        if (materials.size() >= 10) lore.add("§6 ● §8And §e%last §8other block(s)".replace("%last", String.valueOf(materials.size() - 10)));
    }
}
