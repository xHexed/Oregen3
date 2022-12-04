package me.banbeucmas.oregen3.gui.editor.options.generator;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateRandomOraxen {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("§0").build();

    private static final List<io.th0rgal.oraxen.items.ItemBuilder> ORAXEN_BLOCKS;

    static {
        ORAXEN_BLOCKS = new ArrayList<>();

        for (io.th0rgal.oraxen.items.ItemBuilder items : OraxenItems.getItems()) {
            if (OraxenBlocks.isOraxenBlock(OraxenItems.getIdByItem(items))) ORAXEN_BLOCKS.add(items);
        }
    }

    public static void open(Player player, Generator generator) {
        RyseInventory oraxenUI = RyseInventory.builder()
                .title("Oraxen [p.1]")
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
                                .build(), event -> CreateRandomBlock.open(player, generator)));
                        contents.fillRow(45, BORDER);

                        Configuration config = Oregen3.getPlugin().getConfig();

                        ListGenerator.movePage(player, contents, pagination);

                        for (io.th0rgal.oraxen.items.ItemBuilder item : ORAXEN_BLOCKS) {
                            pagination.addItem(IntelligentItem.of(item
                                    .setLore(Arrays.asList("", "§eClick to add oraxen block", ""))
                                    .build(), event -> {
                                // TODO: Save config with comments
                                config.set("generators." + generator.getId() + ".random.oraxen-" + OraxenItems.getIdByItem(item), 1.0);
                                Oregen3.getPlugin().saveConfig();
                                Oregen3.getPlugin().reload();
                                ListRandomBlock.open(player, generator);
                            }));
                        }
                    }
                })
                .build(Oregen3.getPlugin());
        oraxenUI.open(player);
    }
}
