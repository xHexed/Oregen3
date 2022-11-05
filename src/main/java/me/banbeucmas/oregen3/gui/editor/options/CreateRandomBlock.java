package me.banbeucmas.oregen3.gui.editor.options;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.SlotIterator;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryContents;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.gui.editor.MenuGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateRandomBlock {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()).setName("§0").build();

    private static final List<Material> fullItemList = new ArrayList<>();

    private static List<Material> filteredItems;

    public static void open(Player player, Generator generator) {
        RyseInventory blockUI = RyseInventory.builder()
                .title("Choose Block You Want")
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
                                .build(), event -> ListGenerator.open(player)));
                        for (int i = 0; i < 9; i++) contents.set(5, i, BORDER);

                        Configuration config = Oregen3.getPlugin().getConfig();
                        ConfigurationSection path = config.getConfigurationSection("generators." + generator.getId() + ".random");
                        List<String> materials = config.getStringList("generators." + generator.getId() + ".random");

                        for (Material value : Material.values()) {
                            if (value.isAir()) {
                                continue;
                            }
                            fullItemList.add(value);
                        }

                        filteredItems = fullItemList;

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

                        for (Material item : filteredItems) {
                            pagination.addItem(IntelligentItem.of(XMaterial.matchXMaterial(item).parseItem(), event -> {
                                // TODO: Save config with comments
                                config.set("generators." + generator.getId() + ".random." + item.toString(), 1.0);
                                Oregen3.getPlugin().saveConfig();
                                ListRandomBlock.open(player, generator);
                            }));
                        }
                    }
                })
                .build(Oregen3.getPlugin());
        blockUI.open(player);
    }

}
