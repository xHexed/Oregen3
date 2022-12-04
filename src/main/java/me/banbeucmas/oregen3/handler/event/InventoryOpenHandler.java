package me.banbeucmas.oregen3.handler.event;

import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.events.RyseInventoryOpenEvent;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.banbeucmas.oregen3.gui.editor.options.generator.ListRandomBlock;
import me.banbeucmas.oregen3.gui.editor.options.generator.ListWorldGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class InventoryOpenHandler implements Listener {

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
        String page = String.valueOf(pagination.page());

        if (pagination.isFirst()) return;
        switch (String.valueOf(inventory.getIdentifier())) {
            case "ListGenerator":
                contents.updateTitle("Generators [p.%p]".replace("%p", page));
                break;
            case "ListRandomBlock":
                contents.updateTitle("Edit random blocks (%name) [p.%p]".replace("%p", page).replace("%name", ListRandomBlock.GENERATOR_ID));
                break;
            case "ListWorldGenerator":
                contents.updateTitle("Edit world (%name) [p.%p]".replace("%p", page).replace("%name", ListWorldGenerator.GENERATOR_ID));
                break;
            case "ListMusic":
                contents.updateTitle("Choose Music You Want [p.%p]".replace("%p", page));
                break;
            case "ListBlock":
                contents.updateTitle("Choose Block You Want [p.%p]".replace("%p", page));
                break;
            case "ListWorld":
                contents.updateTitle("Choose World You Want [p.%p]".replace("%p", page));
                break;
        }
    }
}
