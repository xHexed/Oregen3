package me.banbeucmas.oregen3.gui.editor.options;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.editor.Editor;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.manager.ui.chest.ChestUI;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MenuSound extends ChestUI {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setName("§0").build();

    public MenuSound(Player player, Generator generator) {
        super(player, "Menu Sound", 5);

        Configuration config = Oregen3.getPlugin().getConfig();

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(2, 2, new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setName("§fEdit Sound")
                .addLore("",
                        "§7Sound: §e" + config.getString("generators." + generator.getId() + ".sound.name"),
                        "",
                        "§eClick to edit sound", "")
                .setSkull("e39343ece629fda4882079a467a5f4dc3b7cbcf6b8a2179bebf2494ee9991a60")
                .build(), null);
        set(6, 2, new ItemBuilder(XMaterial.NOTE_BLOCK)
                .setName("§fEdit Pitch And Volume")
                .addLore("",
                        "§7Volume: §e" + config.getInt("generators." + generator.getId() + ".sound.volume"),
                        "§7Pitch: §e" + config.getInt("generators." + generator.getId() + ".sound.pitch"),
                        "",
                        "§eClick to edit sound", "")
                .build(), event -> {
            event.setCancelled(true);
            player.closeInventory();
            player.sendMessage(new String[] {
                    "",
                    "§eChat volume and pitch you want to set.",
                    "§eExample: <volume>-<pitch> (10-1)",
                    ""
            });
            Editor.markLevelSet(player, generator);
        });
        for (int i = 0; i < 9; i++) set(i, 4, BORDER, null);
    }



    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }

}
