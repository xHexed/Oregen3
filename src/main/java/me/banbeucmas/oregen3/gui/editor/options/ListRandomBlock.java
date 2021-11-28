package me.banbeucmas.oregen3.gui.editor.options;

import com.cryptomorin.xseries.XMaterial;
import io.th0rgal.oraxen.items.OraxenItems;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.editor.Editor;
import me.banbeucmas.oregen3.gui.editor.MenuGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.manager.ui.PlayerUI;
import me.banbeucmas.oregen3.manager.ui.chest.ChestUI;
import me.banbeucmas.oregen3.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListRandomBlock extends ChestUI {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).setName("§0").build();
    protected static final ItemStack NEXT = new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial()).setName("§e <- Go Back ").setSkull("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").build();
    protected static final ItemStack PREVIOUS = new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial()).setName("§e <- Previous Page ").setSkull("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build();

    private Map<String, Double> mmaterial = new HashMap<>();

    public MenuGenerator menuGenerator;
    public Generator generator;
    private int page;

    public ListRandomBlock(Player player, MenuGenerator menuGenerator, Generator generator, int page) {
        super(player, "Edit random blocks (%name)".replace("%name", generator.getId()), 6);
        this.menuGenerator = menuGenerator;
        this.generator = generator;
        this.page = page;

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(0, 0, new ItemBuilder(XMaterial.ARROW.parseMaterial())
                .setName("§e <- Go Back ")
                .build(), event -> {
            PlayerUI.openUI(player, menuGenerator);
        });
        renderPage();
        for (int i = 0; i < 9; i++) set(i, 5, BORDER, null);
        set(4, 5, new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial())
                .setName("§2Add Block")
                .addLore("", "§7Want to add more block? click here!", "")
                .setSkull("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777")
                .build(), event -> {
            CreateRandomBlock ui = new CreateRandomBlock(player, this, generator, page);
            PlayerUI.openUI(player, ui);
        });
    }

    private void renderPage() {

        Configuration config = Oregen3.getPlugin().getConfig();
        ConfigurationSection path = config.getConfigurationSection("generators." + generator.getId() + ".random");
        List<String> materials = new ArrayList<>(path.getKeys(false));

        if (page > 0) set(2, 0, PREVIOUS, event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page--;
            renderPage();
        });
        if ((page + 1) * 36 < materials.size()) set(6, 0, NEXT, event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page++;
            renderPage();
        });

        double totalChances = 0;
        for (String chance : path.getKeys(false)) {
            totalChances += path.getDouble(chance);
            set(4, 0, new ItemBuilder(XMaterial.CHEST_MINECART.parseMaterial())
                    .setName("§7Total Chances: §6" + totalChances + "%")
                    .build(), null);
        }

        for (int i = 0; i < 36; i++) {

            int matIndex = page * 36 + i;
            if (materials.size() <= matIndex) {
                clearSlot(i % 9, 1 + (i / 9));
                continue;
            }

            String material = materials.get(matIndex);

            if (material.startsWith("oraxen-")) {
                if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
                    ItemStack item = OraxenItems.getItemById(material.substring(7)).build();
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Chances: §6" + StringUtils.DOUBLE_FORMAT.format(config.getDouble("generators." + generator.getId() + ".random." + material)) + "%");
                    lore.add("");
                    lore.add("§8[§2Left-Click§8]§e to edit chances");
                    lore.add("§8[§2Right-Click§8]§e to delete");
                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    set(i % 9, 1 + (i / 9), item, event -> {
                        event.setCancelled(true);
                        setCancelDragEvent(true);
                        if (event.isLeftClick()) {
                            player.closeInventory();
                            Editor.markChanceSet(player, generator, material, matIndex);
                        }
                        if (event.isRightClick()) {
                            config.set("generators." + generator.getId() + ".random." + material, null);
                            // TODO: Find way to save config with comments
                            Oregen3.getPlugin().saveConfig();
                            ListRandomBlock ui = new ListRandomBlock(player, menuGenerator, generator, page);
                            PlayerUI.openUI(player, ui);
                        }
                    });
                } else {
                    set(i % 9, 1 + (i / 9), new ItemBuilder(XMaterial.BEDROCK.parseMaterial())
                            .setName("§cCan't Load Block Textures")
                            .addDescription("§7Please check that you have installed §2Oraxen§7 on you server.")
                            .build(), null);
                }
            } else {
                ItemStack item = XMaterial.matchXMaterial(material).get().parseItem();
                ItemMeta meta = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add("§7Chances: §6" + StringUtils.DOUBLE_FORMAT.format(config.getDouble("generators." + generator.getId() + ".random." + material)) + "%");
                lore.add("");
                lore.add("§8[§2Left-Click§8]§e to edit chances");
                lore.add("§8[§2Right-Click§8]§e to delete");
                meta.setLore(lore);
                item.setItemMeta(meta);

                set(i % 9, 1 + (i / 9), item, event -> {
                    event.setCancelled(true);
                    setCancelDragEvent(true);
                    if (event.isLeftClick()) {
                        player.closeInventory();
                        Editor.markChanceSet(player, generator, material, matIndex);
                    }
                    if (event.isRightClick()) {
                        config.set("generators." + generator.getId() + ".random." + material, null);
                        // TODO: Find way to save config with comments
                        Oregen3.getPlugin().saveConfig();
                        ListRandomBlock ui = new ListRandomBlock(player, menuGenerator, generator, page);
                        PlayerUI.openUI(player, ui);
                    }
                });
            }
        }

    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }
}
