package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.gui.InventoryHandler;
import me.banbeucmas.oregen3.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class EditorGUI implements InventoryHandler {
    private Oregen3 plugin;
    private Inventory inv;

    public EditorGUI(Oregen3 plugin) {
        this.plugin = plugin;
        inv = Bukkit.createInventory(this, 9, "Editor Gui");
        inv.setItem(0, new ItemBuilder(XMaterial.FURNACE.get())
                .setName("§7Edit generators")
                .addLore("§7Click to edit generators")
                .build());
        inv.setItem(1, new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                .setName("§7Global Settings")
                .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM0NjdhNTMxOTc4ZDBiOGZkMjRmNTYyODVjNzI3MzRkODRmNWVjODhlMGI0N2M0OTMyMzM2Mjk3OWIzMjNhZiJ9fX0=")
                .build());
        inv.setItem(8, new ItemBuilder(XMaterial.BARRIER.parseItem())
                .setName("§7Close menu")
                .build());
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        switch (event.getSlot()) {
            case 0:
                event.getWhoClicked().openInventory(new GeneratorList(plugin).getInventory());
                break;
            case 1:
                event.getWhoClicked().openInventory(new GlobalGeneratorSettings(plugin).getInventory());
                break;
            case 8:
                event.getWhoClicked().closeInventory();
        }
    }
}
