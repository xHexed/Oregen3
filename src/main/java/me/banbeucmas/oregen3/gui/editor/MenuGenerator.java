package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryContents;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.editor.Editor;
import me.banbeucmas.oregen3.editor.type.EditType;
import me.banbeucmas.oregen3.gui.editor.options.ListRandomBlock;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
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

                        for (int i = 0; i < 9; i++) contents.set(i, BORDER);
                        contents.set(0, IntelligentItem.of(new ItemBuilder(XMaterial.ARROW.parseMaterial())
                                .setName("§e <- Go Back ")
                                .build(), event -> ListGenerator.open(player)));
                        contents.set(0, 4, item);
                        contents.set(2, 1, IntelligentItem.of(new ItemBuilder(XMaterial.COBBLESTONE.parseMaterial())
                                .setName("§rEdit random blocks")
                                .addLore("", "§eClick to edit random blocks", "")
                                .build(), event -> ListRandomBlock.open(player, generator)));
                        contents.set(2, 3, IntelligentItem.of(new ItemBuilder(XMaterial.NAME_TAG.parseMaterial())
                                .setName("§rEdit permission")
                                .addLore("", "§8[§2Left-Click§8]§e to edit permission", "§8[§2Right-Click§8]§e reset to default", "")
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
                        contents.set(2, 5, IntelligentItem.of(new ItemBuilder(XMaterial.PAPER.parseMaterial())
                                .setName("§rEdit Level")
                                .addLore("", "§8[§2Left-Click§8]§e to edit level", "§8[§2Right-Click§8]§e delete level", "")
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
                        contents.set(2,8, IntelligentItem.of(new ItemBuilder(XMaterial.EXPERIENCE_BOTTLE.parseItem())
                                .setName("")
                                .build(), event -> {}));
                        for (int i = 0; i < 9; i++) contents.set(4, i, BORDER);
                    }
                })
                .build(Oregen3.getPlugin());
        menuGenerator.open(player);
    }

}
