package me.banbeucmas.oregen3.handler.event;

import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.events.RyseInventoryOpenEvent;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.banbeucmas.oregen3.gui.editor.options.generator.ChooseMusicGenerator;
import me.banbeucmas.oregen3.gui.editor.options.generator.ListRandomBlock;
import me.banbeucmas.oregen3.gui.editor.options.generator.ListWorldGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class InventoryUpdateTitleEvent implements Listener {

    @EventHandler
    public void updateTitle(RyseInventoryOpenEvent event) {
        Player player = event.getPlayer();
        RyseInventory inventory = event.getInventory();
        InventoryManager manager = inventory.getManager();

        Optional<InventoryContents> optional = manager.getContents(player.getUniqueId());

        if (!optional.isPresent())
            return;

        InventoryContents contents = optional.get();
        Pagination pagination = contents.pagination();

        if (Objects.equals(inventory.getIdentifier(), "ListGenerator")) {
            if (pagination.isFirst()) return;
            contents.updateTitle("Generators [p.%p]".replace("%p", String.valueOf(pagination.page())));
        } else if (Objects.equals(inventory.getIdentifier(), "ListRandomBlock")) {
            if (pagination.isFirst()) return;
            contents.updateTitle("Edit random blocks (%name) [p.%p]".replace("%p", String.valueOf(pagination.page())).replace("%name", ListRandomBlock.GENERATOR_ID));
        } else if (Objects.equals(inventory.getIdentifier(), "ListWorldGenerator")) {
            if (pagination.isFirst()) return;
            contents.updateTitle("Edit world (%name) [p.%p]".replace("%p", String.valueOf(pagination.page())).replace("%name", ListWorldGenerator.GENERATOR_ID));
        } else if (Objects.equals(inventory.getIdentifier(), "ListMusic")) {
            if (pagination.isFirst()) return;
            contents.updateTitle("Choose Music You Want [p.%p]".replace("%p", String.valueOf(pagination.page())));
        } else if (Objects.equals(inventory.getIdentifier(), "ListBlock")) {
            if (pagination.isFirst()) return;
            contents.updateTitle("Choose Block You Want [p.%p]".replace("%p", String.valueOf(pagination.page())));
        } else if (Objects.equals(inventory.getIdentifier(), "ListWorld")) {
            if (pagination.isFirst()) return;
            contents.updateTitle("Choose World You Want [p.%p]".replace("%p", String.valueOf(pagination.page())));
        }
    }
}
