package me.banbeucmas.oregen3.gui.editor.options.generator;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChooseMusicGenerator {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("§0").build();

    private static final List<XSound> SOUNDS;

    static {
        SOUNDS = new ArrayList<>();

        SOUNDS.addAll(Arrays.asList(XSound.values()));
    }

    public static void open(Player player, Generator generator) {
        RyseInventory inventory = RyseInventory.builder()
                .identifier("ListMusic")
                .title("Choose Music You Want [p.1]")
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
                                .build(), event -> MusicGenerator.open(player, generator)));
                        contents.fillRow(45, BORDER);

                        ListGenerator.movePage(player, contents, pagination);

                        Configuration config = Oregen3.getPlugin().getConfig();

                        for (XSound sound : SOUNDS) {
                            // Check if sounds is support
                            if (!sound.isSupported()) continue;

                            pagination.addItem(IntelligentItem.of(new ItemBuilder(XMaterial.MUSIC_DISC_FAR.parseItem())
                                    .setName("§r" + sound.toString())
                                    .addLore(
                                            "",
                                            "§8[§2Shift Left-Click§8]§e play at pitch 1",
                                            "§8[§2Shift Right-Click§8]§e play at pitch 2",
                                            "",
                                            "§eClick to set this sound!"
                                    )
                                    .build(), event -> {
                                if (event.isShiftClick() && event.isLeftClick()) {
                                    sound.play(player.getLocation(), 1f, 1f);
                                    return;
                                }
                                if (event.isShiftClick() && event.isRightClick()) {
                                    sound.play(player.getLocation(), 1f, 2f);
                                    return;
                                }
                                // TODO: Save config with comments
                                config.set("generators." + generator.getId() + ".sound.name", sound.parseSound().toString().toUpperCase());
                                if (!config.isSet("generators." + generator.getId() + ".sound.volume")) config.set("generators." + generator.getId() + ".sound.volume", 1);
                                if (!config.isSet("generators." + generator.getId() + ".sound.pitch")) config.set("generators." + generator.getId() + ".sound.pitch", 1);
                                Oregen3.getPlugin().saveConfig();
                                Oregen3.getPlugin().reload();
                                MusicGenerator.open(player, generator);
                            }));
                        }
                    }
                })
                .build(Oregen3.getPlugin());
        inventory.open(player);
    }
}
