package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.enums.IntelligentType;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryContents;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pattern.ContentPattern;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.channels.AsynchronousChannel;

public class MenuGlobal {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("§0").build();

    public static void open(Player player) {
        RyseInventory globalUI = RyseInventory.builder()
                .title("Global Settings")
                .rows(5)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        ContentPattern pattern = contents.contentPattern();
                        pattern.define(
                                "#########",
                                "xxxxxxxxx",
                                "xxaxbxcxx",
                                "xxxxxxxxx",
                                "####<####"
                        );

                        pattern.set('#', BORDER);
                        pattern.set('<', IntelligentItem.of(new ItemBuilder(XMaterial.ARROW.parseMaterial())
                                .setName("§e <- Go Back ")
                                .build(), event -> EditorGUI.open(player)));
                        pattern.set('a', IntelligentItem.empty(new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                                .setName("")
                                .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmU1ZDEwZjlmMzI0NjU5OTY1OGUwYzZkMDQ0NGU4NzRmZmFjMDE0MTA0NDBjNWNmZWM2ZjE5ZDNhYTg4Zjk0NSJ9fX0=")
                                .build()));
                    }
                })
                .build(Oregen3.getPlugin());
        globalUI.open(player);
    }
}