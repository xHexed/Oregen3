package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.editor.Editor;
import me.banbeucmas.oregen3.gui.editor.options.ListRandomBlock;
import me.banbeucmas.oregen3.gui.editor.options.ListWorlds;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.manager.ui.PlayerUI;
import me.banbeucmas.oregen3.manager.ui.chest.ChestUI;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuGenerator extends ChestUI {

    public Generator generator;

    public MenuGenerator(Player player, ListGenerator listGenerator, Generator generator) {
        super(player, "Generator: %name".replace("%name", generator.getId()), 5);
        this.generator = generator;

        Configuration config = Oregen3.getPlugin().getConfig();
        ConfigurationSection path = config.getConfigurationSection("generators." + generator.getId() + ".random");
        List<String> materials = new ArrayList<>(path.getKeys(true));

        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§7Generator §6" + generator.getId());
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Name: " + generator.getName());
        lore.add("§7Permission: " + generator.getPermission());
        lore.add("§7Priority: " + generator.getPriority());
        lore.add("§7Level: " + generator.getLevel());
        lore.add("");
        if (materials.size() > 0) lore.add("§7Random:");
        for (int mc = 0; mc < materials.size(); mc++){
            lore.add("§6 ● §8" + materials.get(mc) + ":§e " + config.getDouble("generators." + generator.getId() + ".random." + materials.get(mc)) + "%");
        }
        if (materials.size() > 0)lore.add("");
        meta.setLore(lore);
        SkullUtils.applySkin(meta, "http://textures.minecraft.net/texture/7f9f356f5fe7d1bc92cddfaeba3ee773ac9df1cc4d1c2f8fe5f47013032c551d");
        item.setItemMeta(meta);

        for (int i = 0; i < 9; i++) set(i, 0, ListGenerator.BORDER, null);
        set(0, 0, new ItemBuilder(XMaterial.ARROW.parseMaterial())
                .setName("§e <- Go Back ")
                .build(), event -> {
            PlayerUI.openUI(player, listGenerator);
        });
        set(4, 0, item, null);
        set(1, 2, new ItemBuilder(XMaterial.COBBLESTONE.parseMaterial())
                .setName("§rEdit random blocks")
                .addLore("", "§eClick to edit random blocks", "")
                .build(), event -> {
            ListRandomBlock ui = new ListRandomBlock(player, this, generator, 0);
            PlayerUI.openUI(player, ui);
        });
        set(2, 2, new ItemBuilder(XMaterial.CHEST.parseMaterial())
                .setName("§rEdit permission")
                .addLore("", "§7Permission: §6" + config.getString("generators." + generator.getId() + ".permission"),
                        "", "§eClick to edit generator permission",
                        "")
                .build(), event -> {
            player.closeInventory();
            Editor.markPermSet(player, generator);
        });
        set(3, 2, new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial())
                .setName("§rEdit Priority")
                .addLore("", "§7Priority: §6" + config.getInt("generators." + generator.getId() + ".priority"),
                        "", "§eClick to edit generator permission",
                        "")
                .setSkull("6d65ce83f1aa5b6e84f9b233595140d5b6beceb62b6d0c67d1a1d83625ffd")
                .build(), event -> {
            player.closeInventory();
            Editor.markPrioritySet(player, generator);
        });
        set(4, 2, new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial())
                .setName("§rEdit World(s)")
                .addLore("", "§eClick to edit world(s)", "")
                .setSkull("b1dd4fe4a429abd665dfdb3e21321d6efa6a6b5e7b956db9c5d59c9efab25")
                .build(), event -> {
            ListWorlds ui = new ListWorlds(player, this, generator, 0);
            PlayerUI.openUI(player, ui);
        });
        for (int i = 0; i < 9; i++) set(i, 4, ListGenerator.BORDER, null);
    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }

}
