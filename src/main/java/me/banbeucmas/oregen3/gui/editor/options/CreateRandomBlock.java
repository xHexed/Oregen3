package me.banbeucmas.oregen3.gui.editor.options;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.gui.editor.MenuGenerator;
import me.banbeucmas.oregen3.managers.items.ItemBuilder;
import me.banbeucmas.oregen3.managers.items.SkullIndex;
import me.banbeucmas.oregen3.managers.ui.PlayerUI;
import me.banbeucmas.oregen3.managers.ui.chest.ChestUI;
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
import java.util.stream.Collectors;

public class CreateRandomBlock extends ChestUI {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).setName("§0").build();

    private static final List<Material> fullItemList = new ArrayList<>();
    private static final Map<String, Double> mmaterials = new HashMap<>();

    private ListRandomBlock listRandomBlock;
    private Generator generator;
    private int page;
    private List<Material> filteredItems;

    public CreateRandomBlock(Player player, ListRandomBlock listRandomBlock, Generator generator, int page) {
        super(player, "Choose Block You Want [p.%page]"
                .replace("%page", String.valueOf(page + 1)), 6);
        this.listRandomBlock = listRandomBlock;
        this.generator = generator;
        this.page = page;

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(0, 0, new ItemBuilder(XMaterial.ARROW.parseMaterial())
                .setName("§e <- Go Back ")
                .build(), event -> {
            PlayerUI.openUI(player, listRandomBlock);
        });
        renderPage();
        for (int i = 0; i < 9; i++) set(i, 5, BORDER, null);
    }

    private void renderPage() {

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

        if (page > 0) set(2, 0, new ItemBuilder(SkullIndex.PREVIOUS).setName("§e <- Previous Page ").build(), event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page--;
            renderPage();
        });
        if ((page + 1) * 36 < filteredItems.size()) set(6, 0, new ItemBuilder(SkullIndex.NEXT).setName("§e Next Page -> ").build(), event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page++;
            renderPage();
        });

        for (int i = 0; i < 36; i++) {

            int index = page * 36 + i;
            if (filteredItems.size() <= index) {
                clearSlot(i % 9, 1 + (i / 9));
                continue;
            }

            set(i % 9, 1 + (i / 9), XMaterial.matchXMaterial(filteredItems.get(index)).parseItem(), event -> {
                event.setCancelled(true);
                config.set("generators." + generator.getId() + ".random." + filteredItems.get(index).toString(), 1.0);
                Oregen3.getPlugin().saveConfig();
                ListRandomBlock ui = new ListRandomBlock(player,
                        new MenuGenerator(player,
                                new ListGenerator(player,
                                        new EditorGUI(player), 0), generator), generator, 0);
                PlayerUI.openUI(player, ui);
            });

        }
    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }
}
