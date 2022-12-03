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
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.Buffer;
import java.util.List;

public class CreateWorldGenerator {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("§0").build();

    public static void open(Player player, Generator generator) {
        RyseInventory inventory = RyseInventory.builder()
                .identifier("ListWorld")
                .title("Choose World You Want [p.1]")
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
                                .build(), event -> ListGenerator.open(player)));
                        contents.fillRow(45, BORDER);

                        Configuration config = Oregen3.getPlugin().getConfig();
                        List<String> worlds = config.getStringList("generators." + generator.getId() + ".world.list");

                        ListGenerator.movePage(player, contents, pagination);

                        for (World world : Bukkit.getWorlds()) {
                            // Checking if world already in list
                            if (worlds.contains(world.getName())) continue;

                            pagination.addItem(IntelligentItem.of(new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                                    .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM0NjdhNTMxOTc4ZDBiOGZkMjRmNTYyODVjNzI3MzRkODRmNWVjODhlMGI0N2M0OTMyMzM2Mjk3OWIzMjNhZiJ9fX0=")
                                    .setName("§2" + world.getName())
                                    .addLore("", "§eClick to add world", "")
                                    .build(), event -> {
                                // TODO: Save config with comments
                                worlds.add(world.getName());
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
