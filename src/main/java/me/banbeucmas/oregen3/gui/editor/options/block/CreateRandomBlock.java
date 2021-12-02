package me.banbeucmas.oregen3.gui.editor.options.block;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.gui.editor.MenuGenerator;
import me.banbeucmas.oregen3.gui.editor.options.ListRandomBlock;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.manager.ui.PlayerUI;
import me.banbeucmas.oregen3.manager.ui.chest.ChestUI;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CreateRandomBlock extends ChestUI {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setName("§0").build();
    protected static final ItemStack NEXT = new ItemBuilder(XMaterial.PLAYER_HEAD).setName("§e Next Page -> ").setSkull("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").build();
    protected static final ItemStack PREVIOUS = new ItemBuilder(XMaterial.PLAYER_HEAD).setName("§e <- Previous Page ").setSkull("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build();

    private static final List<Material> fullItemList = new ArrayList<>();

    private Generator generator;
    private int page;
    private List<Material> filteredItems;

    public CreateRandomBlock(Player player, Generator generator, int page) {
        super(player, "Choose Block You Want", 6);
        this.generator = generator;
        this.page = page;

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(0, 0, new ItemBuilder(XMaterial.ARROW)
                .setName("§e <- Go Back ")
                .build(), event -> {
            ListRandomBlock ui = new ListRandomBlock(player, generator, page);
            PlayerUI.openUI(player, ui);
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
            if (!value.isBlock()) {
                continue;
            }
            // Check for no duplication
            if (!fullItemList.contains(value)) {
                fullItemList.add(value);
            }
        }
        Collections.sort(fullItemList);
        filteredItems = fullItemList;

        if (page > 0) { set(2, 0, PREVIOUS, event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page--;
            renderPage();
        });} else set(2, 0, BORDER, null);
        if ((page + 1) * 36 < filteredItems.size()) { set(6, 0, NEXT, event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page++;
            renderPage();
        });} else set(6, 0, BORDER, null);

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
                Oregen3.getPlugin().reload();
                ListRandomBlock ui = new ListRandomBlock(player, generator, 0);
                PlayerUI.openUI(player, ui);
            });

        }
    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }
}