package me.banbeucmas.oregen3.gui.editor.options.generator;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import io.th0rgal.oraxen.items.OraxenItems;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.editor.Editor;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.gui.editor.MenuGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ListRandomBlock {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("§0").build();

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

                        contents.fillRow(0, BORDER);
                        contents.set(0, IntelligentItem.of(new ItemBuilder(XMaterial.ARROW.parseMaterial())
                                .setName("§e <- Go Back ")
                                .build(), event -> MenuGenerator.open(player, generator)));
                        contents.fillRow(45, BORDER);

                        contents.set(5, 4, IntelligentItem.of(new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                                .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19")
                                .setName("§2Add Block")
                                .addLore("", "§7Want to add more block? click here!", "")
                                .build(), event -> CreateRandomBlock.open(player, generator)));

                        ListGenerator.movePage(player, contents, pagination);

                        Configuration config = Oregen3.getPlugin().getConfig();
                        ConfigurationSection path = config.getConfigurationSection("generators." + generator.getId() + ".random");
                        List<String> materials = new ArrayList<>(path.getKeys(false));

                        contents.set(0, 4, new ItemBuilder(XMaterial.CHEST_MINECART.parseMaterial())
                                    .setName("§7Total Chances: §6" + generator.getTotalChance())
                                    .build());

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
                                    Oregen3.getPlugin().reload();
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
