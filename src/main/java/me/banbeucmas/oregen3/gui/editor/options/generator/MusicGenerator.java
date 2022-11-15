package me.banbeucmas.oregen3.gui.editor.options.generator;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pattern.ContentPattern;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.editor.Editor;
import me.banbeucmas.oregen3.editor.type.EditType;
import me.banbeucmas.oregen3.gui.editor.MenuGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MusicGenerator {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("§0").build();

    public static void open(Player player, Generator generator) {
        RyseInventory inventory = RyseInventory.builder()
                .title("Music Editor")
                .rows(5)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        ContentPattern pattern = contents.contentPattern();
                        pattern.define(
                                "<########",
                                "xxxxxxxxx",
                                "xx1x2x3xx",
                                "xxxxxxxxx",
                                "#########"
                        );

                        Configuration config = Oregen3.getPlugin().getConfig();

                        pattern.set('#', BORDER);
                        pattern.set('<', IntelligentItem.of(new ItemBuilder(XMaterial.ARROW.parseMaterial())
                                .setName("§e <- Go Back ")
                                .build(), event -> MenuGenerator.open(player, generator)));
                        pattern.set('1', IntelligentItem.of(new ItemBuilder(XMaterial.JUKEBOX.parseItem())
                                .setName("§rEdit Sound")
                                .addLore("", "§7Current sound:§e " + generator.getSound(), "", "§eClick to edit sound", "")
                                .build(), event -> ChooseMusicGenerator.open(player, generator)));
                        pattern.set('2', IntelligentItem.of(new ItemBuilder(XMaterial.NOTE_BLOCK.parseItem())
                                .setName("§rEdit Volume")
                                .addLore("", "§7Current volume:§e " + generator.getSoundVolume(), "", "§eClick to set volume", "")
                                .build(), event -> {
                            player.closeInventory();
                            player.sendMessage("",
                                    "§7Please type in chat volume you would like to set",
                                    "§7Type §ccancel §7to cancel",
                                    "");
                            Editor.markEditType(player, generator, EditType.SET_VOLUME);
                        }));
                        pattern.set('3', IntelligentItem.of(new ItemBuilder(XMaterial.PAPER.parseItem())
                                .setName("§rEdit Pitch")
                                .addLore("", "§7Current pitch:§e " + generator.getSoundPitch(), "", "§eClick to set pitch", "")
                                .build(), event -> {
                            player.closeInventory();
                            player.sendMessage("",
                                    "§7Please type in chat pitch you would like to set",
                                    "§7Type §ccancel §7to cancel",
                                    "");
                            Editor.markEditType(player, generator, EditType.SET_PITCH);
                        }));
                    }
                })
                .build(Oregen3.getPlugin());
        inventory.open(player);
    }
}
