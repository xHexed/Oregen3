package me.banbeucmas.oregen3.gui.editor.options;

import com.cryptomorin.xseries.XMaterial;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.items.OraxenMeta;
import me.banbeucmas.oregen3.manager.ui.chest.ChestUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CreateRandomOraxen extends ChestUI {

    protected static final ItemStack BORDER = new me.banbeucmas.oregen3.manager.items.ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).setName("§0").build();
    protected static final ItemStack NEXT = new me.banbeucmas.oregen3.manager.items.ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial()).setName("§e Next Page -> ").setSkull("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").build();
    protected static final ItemStack PREVIOUS = new me.banbeucmas.oregen3.manager.items.ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial()).setName("§e <- Previous Page ").setSkull("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build();

    private static List<ItemBuilder> fullOraxenList = new ArrayList<>();

    private int page;
    private List<ItemBuilder> filteredItems;

    public CreateRandomOraxen(Player player/**, CreateRandomBlock createRandomBlock, Generator generator*/, int page) {
        super(player, "Choose Oraxen Block You Want", 6);
        this.page = page;
        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
            renderPage();
        } else {
            set(22, new me.banbeucmas.oregen3.manager.items.ItemBuilder(XMaterial.BARRIER.parseMaterial())
                    .setName("§cOraxen Not Found")
                    .addDescription("§7You don't have oraxen plugin installed or enabled to use this menu. Consider to install or check if it's enabled before come back here").build(), null);
        }
        for (int i = 0; i < 9; i++) set(i, 5, BORDER, null);
    }

    private void renderPage() {

        for (ItemBuilder entry : OraxenItems.getItems()) {
            fullOraxenList.add(entry);
        }
        filteredItems = fullOraxenList;

        for (int i = 0; i < 36; i++) {

            if (page > 0) set(2, 0, PREVIOUS, event -> {
                event.setCancelled(true);
                setCancelDragEvent(true);
                page--;
                renderPage();
            });
            if ((page + 1) * 36 < filteredItems.size()) set(6, 0, NEXT, event -> {
                event.setCancelled(true);
                setCancelDragEvent(true);
                page++;
                renderPage();
            });

            int index = page * 36 + i;
            if (filteredItems.size() <= index) {
                clearSlot(i % 9, 1 + (i / 9));
                continue;
            }

            set(i % 9, 1 + (i / 9), filteredItems.get(index).build(), null);

        }

    }
    
    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }
}
