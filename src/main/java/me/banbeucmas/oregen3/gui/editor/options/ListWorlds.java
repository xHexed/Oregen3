package me.banbeucmas.oregen3.gui.editor.options;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.EditorGUI;
import me.banbeucmas.oregen3.gui.editor.ListGenerator;
import me.banbeucmas.oregen3.gui.editor.MenuGenerator;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.manager.ui.PlayerUI;
import me.banbeucmas.oregen3.manager.ui.chest.ChestUI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ListWorlds extends ChestUI {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setName("§0").build();
    protected static final ItemStack NEXT = new ItemBuilder(XMaterial.PLAYER_HEAD).setName("§e Next Page -> ").setSkull("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").build();
    protected static final ItemStack PREVIOUS = new ItemBuilder(XMaterial.PLAYER_HEAD).setName("§e <- Previous Page ").setSkull("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build();

    public Generator generator;
    private int page;

    public ListWorlds(Player player, Generator generator, int page) {
        super(player, "Generator World(s)", 6);
        this.generator = generator;
        this.page = page;

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(0, 0, new ItemBuilder(XMaterial.ARROW)
                .setName("§e <- Go Back ")
                .build(), event -> {
            MenuGenerator ui = new MenuGenerator(player, generator);
            PlayerUI.openUI(player, ui);
        });
        renderPage();
        for (int i = 0; i < 9; i++) set(i, 5, BORDER, null);
        if (Oregen3.getPlugin().getConfig().getBoolean("generators." + generator.getId() + ".world.blacklist")) {
            set(4, 5, new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                    .setName("§2Black List Enabled")
                    .addLore("", "§7Click to §cdisable §7black list.")
                    .build(), event -> {
                Oregen3.getPlugin().getConfig().set("generators." + generator.getId() + ".world.blacklist", false);
                Oregen3.getPlugin().saveConfig();
                Oregen3.getPlugin().reload();
                ListWorlds ui = new ListWorlds(player, generator, 0);
                PlayerUI.openUI(player, ui);
            });
        } else {
            set(4, 5, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                    .setName("§cBlack List Disabled")
                    .addLore("", "§7Click to §2enable §7black list.")
                    .build(), event -> {
                Oregen3.getPlugin().getConfig().set("generators." + generator.getId() + ".world.blacklist", true);
                Oregen3.getPlugin().saveConfig();
                Oregen3.getPlugin().reload();
                ListWorlds ui = new ListWorlds(player, generator, 0);
                PlayerUI.openUI(player, ui);
            });
        }
    }

    private void renderPage() {

        Configuration config = Oregen3.getPlugin().getConfig();
        List<String> worlds = new ArrayList<>();
        List<String> configWorlds = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            if (!worlds.contains(world.getName())) {
                worlds.add(world.getName());
            }
        }

        for (String world : config.getStringList("generators." + generator.getId() + ".world.list")) {
            if (!configWorlds.contains(world)) {
                configWorlds.add(world);
            }
        }

        if (page > 0) { set(2, 0, PREVIOUS, event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page--;
            renderPage();
        });} else set(2, 0, BORDER, null);
        if ((page + 1) * 36 < worlds.size()) { set(6, 0, NEXT, event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page++;
            renderPage();
        });} else set(6, 0, BORDER, null);

        for (int i = 0; i < 36; i++) {

            int worldIndex = page * 36 + i;
            if (worlds.size() <= worldIndex) {
                clearSlot(i % 9, 1 + (i / 9));
                continue;
            }

            String world = worlds.get(worldIndex);

            if (config.getStringList("generators." + generator.getId() + ".world.list").contains(world)) {
                set(i % 9, 1 + (i / 9), new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                        .setName("§7World: §2" + world)
                        .addLore("", "§7Click to §cdisable §7this world", "")
                        .build(), event -> {
                    configWorlds.remove(world);
                    config.set("generators." + generator.getId() + ".world.list", configWorlds);
                    Oregen3.getPlugin().saveConfig();
                    Oregen3.getPlugin().reload();
                    ListWorlds ui = new ListWorlds(player, generator, 0);
                    PlayerUI.openUI(player, ui);
                });
            } else {
                set(i % 9, 1 + (i / 9), new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                        .setName("§7World: §c" + world)
                        .addLore("", "§7Click to §2enable §7this world", "")
                        .build(), event -> {
                    configWorlds.add(world);
                    config.set("generators." + generator.getId() + ".world.list", configWorlds);
                    Oregen3.getPlugin().saveConfig();
                    Oregen3.getPlugin().reload();
                    ListWorlds ui = new ListWorlds(player, generator, 0);
                    PlayerUI.openUI(player, ui);
                });
            }
        }
    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }

}
