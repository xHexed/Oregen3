package me.banbeucmas.oregen3.gui.editor.options.generator;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.gui.editor.MenuGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ListWorldGenerator {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("§0").build();
    protected static IntelligentItem ENABLE = null;
    protected static IntelligentItem DISABLE = null;

    public static void open(Player player, Generator generator) {
        RyseInventory inventory = RyseInventory.builder()
                .title("Edit world (%name)".replace("%name", generator.getId()))
                .rows(6)
                .ignoredSlots(48)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        Pagination pagination = contents.pagination();
                        pagination.setItemsPerPage(36);
                        pagination.iterator(SlotIterator.builder().startPosition(1, 0).type(SlotIterator.SlotIteratorType.HORIZONTAL).build());

                        Configuration config = Oregen3.getPlugin().getConfig();

                        contents.fillRow(0, BORDER);
                        contents.set(0, IntelligentItem.of(new ItemBuilder(XMaterial.ARROW.parseMaterial())
                                .setName("§e <- Go Back ")
                                .build(), event -> MenuGenerator.open(player, generator)));
                        contents.fillRow(45, BORDER);

                        ENABLE = IntelligentItem.empty(new ItemBuilder(XMaterial.LIME_STAINED_GLASS_PANE.parseItem())
                                .setName("§7World Blacklist: §aEnable")
                                .addLore("", "§eCheck to disable.", "")
                                .build());
                        DISABLE = IntelligentItem.empty(new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem())
                                .setName("§7World Blacklist: §cDisable")
                                .addLore("", "§eCheck to enable.", "")
                                .build());

                        contents.set(5,3, config.getBoolean("generators." + generator.getId() + ".world.blacklist") ? ENABLE : DISABLE);
                        contents.addAdvancedSlot(48, event -> {
                            event.setCancelled(true);
                            if (config.getBoolean("generators." + generator.getId() + ".world.blacklist")) {
                                // TODO: Save config with comments
                                config.set("generators." + generator.getId() + ".world.blacklist", false);
                                Oregen3.getPlugin().saveConfig();
                                Oregen3.getPlugin().reload();
                                contents.updateOrSet(48, DISABLE);
                                ListWorldGenerator.open(player, generator);
                            } else {
                                // TODO: Save config with comments
                                config.set("generators." + generator.getId() + ".world.blacklist", true);
                                Oregen3.getPlugin().saveConfig();
                                Oregen3.getPlugin().reload();
                                contents.updateOrSet(48, ENABLE);
                                ListWorldGenerator.open(player, generator);
                            }
                        });

                        contents.set(5, 4, IntelligentItem.of(new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                                .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM0NjdhNTMxOTc4ZDBiOGZkMjRmNTYyODVjNzI3MzRkODRmNWVjODhlMGI0N2M0OTMyMzM2Mjk3OWIzMjNhZiJ9fX0=")
                                .setName("§2Add World")
                                .addLore("", "§7Want to add more world? click here!", "")
                                .build(), event -> CreateWorldGenerator.open(player, generator)));

                        ListGenerator.movePage(player, contents, pagination);

                        List<String> worlds = config.getStringList("generators." + generator.getId() + ".world.list");

                        for (String world : worlds) {
                            pagination.addItem(IntelligentItem.of(new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                                    .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM0NjdhNTMxOTc4ZDBiOGZkMjRmNTYyODVjNzI3MzRkODRmNWVjODhlMGI0N2M0OTMyMzM2Mjk3OWIzMjNhZiJ9fX0=")
                                    .setName("§2" + world)
                                    .addLore("", "§7Click to remove world!", "")
                                    .build(), event -> {
                                // TODO: Save config with comments
                                worlds.remove(world);
                                config.set("generators." + generator.getId() + ".world.list", worlds);
                                Oregen3.getPlugin().saveConfig();
                                Oregen3.getPlugin().reload();
                                ListWorldGenerator.open(player, generator);
                            }));
                        }
                    }
                })
                .build(Oregen3.getPlugin());
        inventory.open(player);
    }

}
