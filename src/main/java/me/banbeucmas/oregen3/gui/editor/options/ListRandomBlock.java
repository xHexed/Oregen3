package me.banbeucmas.oregen3.gui.editor.options;

import com.bgsoftware.common.config.CommentedConfiguration;
import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.SlotIterator;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryContents;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.th0rgal.oraxen.items.OraxenItems;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.editor.Editor;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.gui.editor.MenuGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListRandomBlock {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()).setName("§0").build();

    public static void open(Player player, Generator generator) {
        RyseInventory randomUI = RyseInventory.builder()
                .title("Edit random blocks (%name)".replace("%name", generator.getId()))
                .rows(6)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        Pagination pagination = contents.pagination();
                        pagination.setItemsPerPage(36);
                        pagination.iterator(SlotIterator.builder().startPosition(1, 0).type(SlotIterator.SlotIteratorType.HORIZONTAL).build());

                        for (int i = 0; i < 9; i++) contents.set(i, BORDER);
                        for (int i = 0; i < 9; i++) contents.set(5, i, BORDER);

                        contents.set(5, 4, IntelligentItem.of(new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial())
                                .setName("§2Add Block")
                                .addLore("", "§7Want to add more block? click here!", "")
                                .build(), event -> CreateRandomBlock.open(player, generator)));

                        contents.set(5, 2, IntelligentItem.of(new ItemBuilder(Material.ARROW).setAmount((pagination.isFirst() ? 1 : pagination.page() - 1)).setName("§e <- Previous Page ").build(), event -> {
                            if (pagination.isFirst()) {
                                MenuGenerator.open(player, generator);
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

                        Configuration config = Oregen3.getPlugin().getConfig();
                        ConfigurationSection path = config.getConfigurationSection("generators." + generator.getId() + ".random");
                        List<String> materials = new ArrayList<>(path.getKeys(false));

                        double totalChances = 0;
                        for (String chance : path.getKeys(false)) {
                            totalChances += path.getDouble(chance);
                            contents.set(0, 4, new ItemBuilder(XMaterial.CHEST_MINECART.parseMaterial())
                                    .setName("§7Total Chances: §6" + totalChances)
                                    .build());
                        }

                        for (String material : materials) {

                            if (material.startsWith("oraxen-")) {
                                if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
                                    ItemStack item = OraxenItems.getItemById(material.substring(7)).build();
                                    ItemMeta meta = item.getItemMeta();
                                    item.setItemMeta(meta);

                                    pagination.addItem(item);
                                } else pagination.addItem(XMaterial.BEDROCK.parseItem());
                            }

                            ItemStack item = XMaterial.matchXMaterial(material).get().parseItem();
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = new ArrayList<>();
                            lore.add("");
                            lore.add("§7Chances: §6" + StringUtils.DOUBLE_FORMAT.format(config.getDouble("generators." + generator.getId() + ".random." + material)) + "%");
                            lore.add("");
                            lore.add("§8[§2Left-Click§8]§e to edit chances");
                            lore.add("§8[§2Right-Click§8]§e to delete");
                            meta.setLore(lore);
                            item.setItemMeta(meta);

                            pagination.addItem(IntelligentItem.of(item, event -> {
                                if (event.isLeftClick()) {
                                    pagination.inventory().close(player);
                                    player.sendMessage("",
                                            "§7Please type in chat how much you're willing to set chance percent",
                                            "§7Type §ccancel §7to cancel",
                                            "");
                                    Editor.markChanceSet(player, generator, material);
                                }

                                if (event.isRightClick()) {
                                    // TODO: Save config with comments
                                    config.set("generators." + generator.getId() + ".random." + material, null);
                                    Oregen3.getPlugin().saveConfig();
                                    ListRandomBlock.open(player, generator);
                                }
                            }));
                        }
                    }
                })
                .build(Oregen3.getPlugin());
        randomUI.open(player);
    }
}
