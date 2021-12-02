package me.banbeucmas.oregen3.gui.editor.options.sound;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.editor.options.MenuSound;
import me.banbeucmas.oregen3.manager.items.ItemBuilder;
import me.banbeucmas.oregen3.manager.ui.PlayerUI;
import me.banbeucmas.oregen3.manager.ui.chest.ChestUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.banbeucmas.oregen3.Oregen3.getPlugin;

public class ChooseSound extends ChestUI {

    protected static final ItemStack BORDER = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setName("§0").build();
    protected static final ItemStack NEXT = new ItemBuilder(XMaterial.PLAYER_HEAD).setName("§e Next Page -> ").setSkull("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").build();
    protected static final ItemStack PREVIOUS = new ItemBuilder(XMaterial.PLAYER_HEAD).setName("§e <- Previous Page ").setSkull("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").build();

    private static final List<Sound> fullSoundList = new ArrayList<>();
    private List<Sound> filteredSounds;

    private Generator generator;
    private int page;

    public ChooseSound(Player player, Generator generator, int page) {
        super(player, "Choose Sound You Want", 6);
        this.generator = generator;
        this.page = page;

        for (int i = 0; i < 9; i++) set(i, 0, BORDER, null);
        set(0, 0, new ItemBuilder(XMaterial.ARROW)
                .setName("§e <- Go Back ")
                .build(), event -> {
            MenuSound ui = new MenuSound(player, generator);
            PlayerUI.openUI(player, ui);
        });
        renderPage();
        for (int i = 0; i < 9; i++) set(i, 5, BORDER, null);
    }

    private void renderPage() {

        Configuration config = Oregen3.getPlugin().getConfig();
        ConfigurationSection path = getPlugin().getConfig().getConfigurationSection("generators." + generator.getId() + ".sound.name");

        for (Sound sound : Sound.values()) {
            // Check for no duplication
            if (!fullSoundList.contains(sound)) {
                fullSoundList.add(sound);
            }
        }
        Collections.sort(fullSoundList);
        filteredSounds = fullSoundList;

        if (page > 0) { set(2, 0, PREVIOUS, event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page--;
            renderPage();
        });} else set(2, 0, BORDER, null);
        if ((page + 1) * 36 < filteredSounds.size()) { set(6, 0, NEXT, event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            page++;
            renderPage();
        });} else set(6, 0, BORDER, null);

        for (int i = 0; i < 36; i++) {

            int index = page * 36 + i;
            if (filteredSounds.size() <= index) {
                clearSlot(i % 9, 1 + (i / 9));
                continue;
            }

            String sound = filteredSounds.get(index).name();

            set(i % 9, 1 + (i / 9), new ItemBuilder(XMaterial.MUSIC_DISC_13)
                    .setName("§7Sound: §2" + sound)
                    .addLore("",
                            "§8᛫§7 Hotbar 0 for pitch 1",
                            "§8᛫§7 Hotbar 1 for pitch 2",
                            "§8᛫§7 Hotbar 2 for pitch 3",
                            "",
                            "§eClick to choose sound",
                            "")
                    .build(), event -> {
                event.setCancelled(true);
                if (event.getHotbarButton() == 0) {
                    player.playSound(player.getLocation(), XSound.matchXSound(sound).get().parseSound(), 1f, 0f);
                    return;
                }
                if (event.isRightClick()) {
                    player.playSound(player.getLocation(), XSound.matchXSound(sound).get().parseSound(), 1f, 1f);
                    return;
                }
                if (event.isShiftClick()) {
                    player.playSound(player.getLocation(), XSound.matchXSound(sound).get().parseSound(), 1f, 2f);
                    return;
                }
                config.set("generators." + generator.getId() + ".sound.name", XSound.matchXSound(sound).get().parseSound());
                Oregen3.getPlugin().saveConfig();
                Oregen3.getPlugin().reload();
                MenuSound ui = new MenuSound(player, generator);
                PlayerUI.openUI(player, ui);
            });
        }
    }

    @Override
    public void failback(InventoryClickEvent event) { event.setCancelled(true); }

}
