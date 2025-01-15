package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.gui.InventoryHandler;
import me.banbeucmas.oregen3.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class GlobalGeneratorSettings implements InventoryHandler {
    private Oregen3 plugin;
    private Inventory inv;

    public GlobalGeneratorSettings(Oregen3 plugin) {
        this.plugin = plugin;
        inv = Bukkit.createInventory(this, 9, "Global Settings");
        inv.setItem(8, new ItemBuilder(XMaterial.ARROW.get())
                .setName("§e<- Go Back")
                .build());
        inv.setItem(2, new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                .setName("")
                .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmU1ZDEwZjlmMzI0NjU5OTY1OGUwYzZkMDQ0NGU4NzRmZmFjMDE0MTA0NDBjNWNmZWM2ZjE5ZDNhYTg4Zjk0NSJ9fX0=")
                .build());
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public void onClick(final InventoryClickEvent event) {
        int slot = event.getSlot();
        switch (slot) {
            case 8:
                event.getWhoClicked().openInventory(new EditorGUI(plugin).getInventory());
                break;
            case 2:
                event.setCancelled(true);
                break;
        }
    }
}