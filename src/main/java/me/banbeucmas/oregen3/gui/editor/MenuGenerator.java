package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItemAnimatorType;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.enums.TimeSetting;
import io.github.rysefoxx.inventory.plugin.pagination.IntelligentMaterialAnimator;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryContents;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.editor.Editor;
import me.banbeucmas.oregen3.editor.type.EditType;
import me.banbeucmas.oregen3.gui.editor.options.generator.ListRandomBlock;
import me.banbeucmas.oregen3.gui.editor.options.generator.ListWorldGenerator;
import me.banbeucmas.oregen3.gui.editor.options.generator.MusicGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuGenerator {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("§0").build();

    public static void open(Player player, Generator generator) {
        RyseInventory menuGenerator = RyseInventory.builder()
                .title("Generator: %name".replace("%name", generator.getId()))
                .rows(5)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {

                        Configuration config = Oregen3.getPlugin().getConfig();
                        ConfigurationSection path = config.getConfigurationSection("generators." + generator.getId() + ".random");
                        List<String> materials = new ArrayList<>(path.getKeys(true));

                        ItemStack item = XMaterial.FURNACE.parseItem();

                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName("§7Generator §6" + generator.getId());
                        List<String> lore = new ArrayList<>();
                        lore.add("");
                        lore.add("§7Name: " + generator.getName());
                        lore.add("§7Permission: " + generator.getPermission());
                        lore.add("§7Priority: " + generator.getPriority());
                        lore.add("§7Level: " + generator.getLevel());
                        lore.add("");
                        if (materials.size() > 0) lore.add("§7Random:");
                        ListGenerator.listRandom(config, materials, lore, generator);
                        if (materials.size() > 0) lore.add("");
                        meta.setLore(lore);
                        item.setItemMeta(meta);

                        contents.fillRow(0, BORDER);
                        contents.set(0, IntelligentItem.of(new ItemBuilder(XMaterial.ARROW.parseMaterial())
                                .setName("§e <- Go Back ")
                                .build(), event -> ListGenerator.open(player)));
                        contents.set(0, 4, item);
                        contents.fillRow(36, BORDER);

//                        IntelligentItem animateItem = IntelligentItem.empty(new ItemBuilder(XMaterial.COBBLESTONE.parseMaterial())
//                                .setName("§rEdit random blocks")
//                                .addLore("", "§eClick to edit random blocks", "")
//                                .build());
//                        contents.set(19, animateItem);
//
//                        IntelligentMaterialAnimator materialAnimator = IntelligentMaterialAnimator.builder(Oregen3.getPlugin())
//                                .item(animateItem)
//                                .slot(19)
//                                .loop()
//                                .delay(5, TimeSetting.SECONDS)
//                                .period(3, TimeSetting.MILLISECONDS)
//                                .materials(Arrays.asList('A', 'B', 'C', 'D', 'E'),
//                                        XMaterial.COAL_BLOCK.parseMaterial(),
//                                        XMaterial.IRON_BLOCK.parseMaterial(),
//                                        XMaterial.GOLD_BLOCK.parseMaterial(),
//                                        XMaterial.DIAMOND_BLOCK.parseMaterial(),
//                                        XMaterial.EMERALD_BLOCK.parseMaterial()
//                                        )
//                                .frame("ABCDE")
//                                .build(contents);
//                        materialAnimator.animate();

                        contents.set(2, 0, IntelligentItem.of(new ItemBuilder(XMaterial.COBBLESTONE.parseMaterial())
                                .setName("§rEdit random blocks")
                                .addLore("", "§eClick to open random blocks", "")
                                .build(), event -> ListRandomBlock.open(player, generator)));

                        contents.set(2, 2, IntelligentItem.of(new ItemBuilder(XMaterial.NAME_TAG.parseMaterial())
                                .setName("§rEdit permission")
                                .addLore("", "§7Current permission:§e " + generator.getPermission(), "", "§8[§2Left-Click§8]§e to edit permission", "§8[§2Right-Click§8]§e reset to default", "")
                                .build(),
                                event -> {
                                    if (event.isLeftClick()) {
                                        player.closeInventory();
                                        player.sendMessage("",
                                                "§7Please type in chat permission you would like to set",
                                                "§7Type §ccancel §7to cancel",
                                                "");
                                        Editor.markEditType(player, generator, EditType.SET_PERMISSION);
                                    }
                                    if (event.isRightClick()) {
                                        // TODO: Save config with comments
                                        config.set("generators." + generator.getId() + ".permission", null);
                                        Oregen3.getPlugin().saveConfig();
                                        Oregen3.getPlugin().reload();
                                        ListGenerator.open(player);
                                    }
                        }));
                        contents.set(2,4, IntelligentItem.of(new ItemBuilder(XMaterial.PAPER.parseItem())
                                .setName("§rEdit Priority")
                                .addLore("", "§7Current priority:§e " + generator.getPriority(), "", "§8[§2Left-Click§8]§e to edit priority", "§8[§2Right-Click§8]§e delete priority", "")
                                .build(), event -> {
                            if (event.isLeftClick()) {
                                player.closeInventory();
                                player.sendMessage("",
                                                   "§7Please type in chat priority you would like to set",
                                                   "§7Type §ccancel §7to cancel",
                                                   "");
                                Editor.markEditType(player, generator, EditType.SET_PRIORITY);
                            }
                            if (event.isRightClick()) {
                                // TODO: Save config with comments
                                config.set("generators." + generator.getId() + ".priority", null);
                                Oregen3.getPlugin().saveConfig();
                                Oregen3.getPlugin().reload();
                                ListGenerator.open(player);
                            }
                        }));
                        contents.set(2, 6, IntelligentItem.of(new ItemBuilder(XMaterial.EXPERIENCE_BOTTLE.parseMaterial())
                                .setName("§rEdit Level")
                                .addLore("", "§7Current level:§e " + generator.getLevel(), "", "§8[§2Left-Click§8]§e to edit level", "§8[§2Right-Click§8]§e delete level", "")
                                .build(), event -> {
                            if (event.isLeftClick()) {
                                player.closeInventory();
                                player.sendMessage("",
                                        "§7Please type in chat level you would like to set",
                                        "§7Type §ccancel §7to cancel",
                                        "");
                                Editor.markEditType(player, generator, EditType.SET_LEVEL);
                            }
                            if (event.isRightClick()) {
                                // TODO: Save config with comments
                                config.set("generators." + generator.getId() + ".level", null);
                                Oregen3.getPlugin().saveConfig();
                                Oregen3.getPlugin().reload();
                                ListGenerator.open(player);
                            }
                        }));
                        contents.set(2, 8, IntelligentItem.of(new ItemBuilder(XMaterial.JUKEBOX.parseMaterial())
                                .setName("§rEdit music")
                                .addLore("", "§eClick to open music", "")
                                .build(), event -> MusicGenerator.open(player, generator)));
                        contents.set(4, 8, IntelligentItem.of(new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                                .setName("§rEdit Worlds")
                                .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM0NjdhNTMxOTc4ZDBiOGZkMjRmNTYyODVjNzI3MzRkODRmNWVjODhlMGI0N2M0OTMyMzM2Mjk3OWIzMjNhZiJ9fX0=")
                                .build(), event -> ListWorldGenerator.open(player, generator)));
                    }
                })
                .build(Oregen3.getPlugin());
        menuGenerator.open(player);
    }

}
