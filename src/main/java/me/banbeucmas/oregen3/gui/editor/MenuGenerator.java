package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryContents;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.editor.options.ListRandomBlock;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuGenerator {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()).setName("§0").build();

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
                        for (int mc = 0; mc < materials.size(); mc++) {
                            lore.add("§6 ● §8" + materials.get(mc) + ":§e " + config.getDouble("generators." + generator.getId() + ".random." + materials.get(mc)));
                        }
                        if (materials.size() > 0) lore.add("");
                        meta.setLore(lore);
                        item.setItemMeta(meta);

                        for (int i = 0; i < 9; i++) contents.set(i, BORDER);
                        contents.set(0, IntelligentItem.of(new ItemBuilder(XMaterial.ARROW.parseMaterial())
                                .setName("§e <- Go Back ")
                                .build(), event -> ListGenerator.open(player)));
                        contents.set(0, 4, item);
                        contents.set(2, 2, IntelligentItem.of(new ItemBuilder(XMaterial.COBBLESTONE.parseMaterial())
                                .setName("§rEdit random blocks")
                                .addLore("", "§eClick to edit random blocks", "")
                                .build(), event -> ListRandomBlock.open(player, generator)));
                        for (int i = 0; i < 9; i++) contents.set(4, i, BORDER);
                    }
                })
                .build(Oregen3.getPlugin());
        menuGenerator.open(player);
    }

}
