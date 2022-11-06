package me.banbeucmas.oregen3.gui;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryContents;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pattern.ContentPattern;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EditorGUI {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("§0").build();

    public static void open(Player player) {
        RyseInventory editor = RyseInventory.builder()
                .title("Editor Gui")
                .rows(5)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        ContentPattern pattern = contents.contentPattern();
                        pattern.define(
                                "#########",
                                "xxxxxxxxx",
                                "xx1x2x3xx",
                                "xxxxxxxxx",
                                "#########"
                        );

                        pattern.set('#', BORDER);
                        pattern.set('1', IntelligentItem.of(
                                new ItemBuilder(XMaterial.FURNACE.parseMaterial())
                                    .setName("§7Edit generators")
                                    .addLore("", "§7Click to edit generators", "")
                                    .build()
                                , event -> ListGenerator.open(player))
                        );
                        pattern.set('2', IntelligentItem.of(
                                new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                                        .setName("§7Global Settings")
                                        .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM0NjdhNTMxOTc4ZDBiOGZkMjRmNTYyODVjNzI3MzRkODRmNWVjODhlMGI0N2M0OTMyMzM2Mjk3OWIzMjNhZiJ9fX0=")
                                        .build(), event -> {}
                        ));
                    }
                })
                .build(Oregen3.getPlugin());
        editor.open(player);
    }
}
