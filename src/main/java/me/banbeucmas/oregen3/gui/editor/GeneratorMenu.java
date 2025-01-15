package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.GUICommons;
import me.banbeucmas.oregen3.gui.InventoryHandler;
import me.banbeucmas.oregen3.gui.editor.generator.GeneratorListRandomBlock;
import me.banbeucmas.oregen3.gui.editor.generator.GeneratorListWorld;
import me.banbeucmas.oregen3.gui.editor.generator.GeneratorSound;
import me.banbeucmas.oregen3.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GeneratorMenu implements InventoryHandler {
    private Inventory inv;
    private Oregen3 plugin;
    private Generator generator;

    public GeneratorMenu(Oregen3 plugin, Generator generator) {
        this.plugin = plugin;
        this.generator = generator;
        inv = Bukkit.createInventory(this, 9, "Generator: " + generator.getId());
        inv.setItem(0, GUICommons.createGeneratorIcon(generator).build());
        inv.setItem(1, GUICommons.BORDER_ICON);
        inv.setItem(2, new ItemBuilder(XMaterial.COBBLESTONE.get())
                .setName("§rEdit random blocks")
                .addLore("", "§eClick to open random blocks", "")
                .build());
        inv.setItem(3, new ItemBuilder(XMaterial.NAME_TAG.get())
                .setName("§rEdit permission")
                .addLore("", "§7Current permission:§e " + generator.getPermission(), "", "§8[§2Left-Click§8]§e to edit permission", "§8[§2Right-Click§8]§e reset to default", "")
                .build());
        inv.setItem(4, new ItemBuilder(XMaterial.PAPER.parseItem())
                .setName("§rEdit Priority")
                .addLore("", "§7Current priority:§e " + generator.getPriority(), "", "§8[§2Left-Click§8]§e to edit priority", "§8[§2Right-Click§8]§e delete priority", "")
                .build());
        inv.setItem(5, new ItemBuilder(XMaterial.EXPERIENCE_BOTTLE.get())
                .setName("§rEdit Level")
                .addLore("", "§7Current level:§e " + generator.getLevel(), "", "§8[§2Left-Click§8]§e to edit level", "§8[§2Right-Click§8]§e delete level", "")
                .build());
        inv.setItem(6, new ItemBuilder(XMaterial.JUKEBOX.get())
                .setName("§rEdit music")
                .addLore("", "§eClick to open music", "")
                .build());
        inv.setItem(7, new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                .setName("§rEdit Worlds")
                .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM0NjdhNTMxOTc4ZDBiOGZkMjRmNTYyODVjNzI3MzRkODRmNWVjODhlMGI0N2M0OTMyMzM2Mjk3OWIzMjNhZiJ9fX0=")
                .build());
        inv.setItem(8, new ItemBuilder(XMaterial.ARROW.get())
                .setName("§e<- Go Back")
                .build());
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        HumanEntity player = event.getWhoClicked();
        switch (slot) {
            case 2:
                player.openInventory(new GeneratorListRandomBlock(plugin, generator).getInventory());
                break;
            case 3:
                if (event.isLeftClick()) {
                    player.closeInventory();
                    player.sendMessage("§7Type in chat permission you would like to set, §7type §ccancel §7to cancel");
                    plugin.getChatListener().addChatAction(player, (perm) -> {
                        if (perm.equals("cancel")) {
                            player.sendMessage("Cancelled");
                        }
                        else {
                            plugin.getConfigManager().setConfig(config -> config.set("generators." + generator.getId() + ".permission", perm));
                            generator.setPermission(perm);
                        }
                        player.openInventory(new GeneratorMenu(plugin, generator).getInventory());
                    });
                }
                if (event.isRightClick()) {
                    plugin.getConfigManager().setConfig(config -> config.set("generators." + generator.getId() + ".permission", null));
                    generator.setPermission("oregen3.generator." + generator.getId());
                    player.openInventory(new GeneratorMenu(plugin, generator).getInventory());
                }
                break;
            case 4:
                if (event.isLeftClick()) {
                    player.closeInventory();
                    player.sendMessage("§7Type in chat priority you would like to set, §7type §ccancel §7to cancel");
                    plugin.getChatListener().addChatAction(player, (priority) -> {
                        if (priority.equals("cancel")) {
                            player.sendMessage("Cancelled");
                        } else {
                            try {
                                long priorityNumber = Long.parseLong(priority);
                                plugin.getConfigManager().setConfig(config ->
                                        config.set("generators." + generator.getId() + ".priority", priorityNumber));
                                generator.setPriority(priorityNumber);
                            } catch (NumberFormatException ex) {
                                player.sendMessage(priority + " is not a number...");
                            }
                        }
                        player.openInventory(new GeneratorMenu(plugin, generator).getInventory());
                    });
                }
                if (event.isRightClick()) {
                    plugin.getConfigManager().setConfig(config -> config.set("generators." + generator.getId() + ".priority", null));
                    generator.setPriority(0);
                    player.openInventory(new GeneratorMenu(plugin, generator).getInventory());
                }
                break;
            case 5:
                if (event.isLeftClick()) {
                    player.closeInventory();
                    player.sendMessage("§7Type in chat level you would like to set, §7type §ccancel §7to cancel");
                    plugin.getChatListener().addChatAction(player, (level) -> {
                        if (level.equals("cancel")) {
                            player.sendMessage("Cancelled");
                        }
                        else {
                            try {
                                double levelNum = Double.parseDouble(level);
                                plugin.getConfigManager().setConfig(config ->
                                        config.set("generators." + generator.getId() + ".level", levelNum));
                                generator.setLevel(levelNum);
                            } catch (NumberFormatException ex) {
                                player.sendMessage(level + " is not a number...");
                            }
                        }
                        player.openInventory(new GeneratorMenu(plugin, generator).getInventory());
                    });
                }
                if (event.isRightClick()) {
                    plugin.getConfigManager().setConfig(config -> config.set("generators." + generator.getId() + ".level", null));
                    generator.setLevel(0);
                    player.openInventory(new GeneratorMenu(plugin, generator).getInventory());
                }
                break;
            case 6:
                player.openInventory(new GeneratorSound(plugin, generator).getInventory());
                break;
            case 7:
                if (!generator.isWorldEnabled()) break;
                player.openInventory(new GeneratorListWorld(plugin, generator).getInventory());
                break;
            case 8:
                player.openInventory(new GeneratorList(plugin).getInventory());
                break;
            default:
                event.setCancelled(true);
        }
    }
}
