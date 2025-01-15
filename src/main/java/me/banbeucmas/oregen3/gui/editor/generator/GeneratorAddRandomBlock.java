package me.banbeucmas.oregen3.gui.editor.generator;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.GUICommons;
import me.banbeucmas.oregen3.gui.PagedInventory;
import me.banbeucmas.oregen3.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GeneratorAddRandomBlock extends PagedInventory<GeneratorAddRandomBlock.DisplayableBlock> {
    private static final List<DisplayableBlock> BLOCK_MATERIALS = new ArrayList<>();

    static {
        for (Material material : Material.values()) {
            // Two values for now...
            if (material.equals(XMaterial.WATER.get())) {
                BLOCK_MATERIALS.add(new DisplayableBlock(material, XMaterial.WATER_BUCKET.get()));
            }
            else if (material.equals(XMaterial.LAVA.get())) {
                BLOCK_MATERIALS.add(new DisplayableBlock(material, XMaterial.LAVA_BUCKET.get()));
            }
            else if (material.isBlock()) {
                BLOCK_MATERIALS.add(new DisplayableBlock(material, material));
            }
        }
    }

    private Oregen3 plugin;
    private Generator generator;

    public GeneratorAddRandomBlock(Oregen3 plugin, Generator generator) {
        this(plugin, generator, 1);
    }

    public GeneratorAddRandomBlock(Oregen3 plugin, Generator generator, int page) {
        super(BLOCK_MATERIALS, "Choose Block ("+ generator.getId() + ") [page %page%]", page);
        this.plugin = plugin;
        this.generator = generator;
    }

    @Override
    public ItemStack getContentIcon(DisplayableBlock item) {
        return buildIcon(item.display, item.block);
    }

    private ItemStack buildIcon(Material material, Material real) {
        try {
            return new ItemBuilder(material).setName(real.name()).addLore("", "§eClick to add block", "").build();
        } catch (IllegalArgumentException e) {
            return new ItemBuilder(XMaterial.BARRIER.get()).setName(real.name()).addLore("§7*Block cant be displayed in inventory*", "§eClick to add block", "").build();
        }
    }

    @Override
    public void getContentAction(DisplayableBlock material, InventoryClickEvent event) {
        if (event.isLeftClick()) {
            addBlock(material.block, event, 1.0);
        } else if (event.isRightClick()) {
            HumanEntity player = event.getWhoClicked();
            player.sendMessage("Type chance of new block " + material + "in generator " + generator.getId() + ", or cancel to abort");
            plugin.getChatListener().addChatAction(player, (chance) -> {
                if (chance.equals("cancel")) {
                    player.sendMessage("Cancelled");
                } else {
                    try {
                        addBlock(material.block, event, Double.parseDouble(chance));
                    } catch (NumberFormatException ex) {
                        player.sendMessage(chance + " is not a number...");
                    }
                }
                event.getWhoClicked().openInventory(new GeneratorAddRandomBlock(plugin, generator, getPage()).getInventory());
            });
        }
    }

    private void addBlock(Material material, InventoryClickEvent event, double chance) {
        plugin.getConfigManager().setConfig(config -> config.set("generators." + generator.getId() + ".random." + material, chance));
        generator.addRandomBlock(material.name(), chance);
        event.getWhoClicked().openInventory(new GeneratorListRandomBlock(plugin, generator).getInventory());
    }

    @Override
    public void addOtherIcons() {
        setItemWithAction(getSize() - 1, GUICommons.GO_BACK_ICON,
                (event) -> event.getWhoClicked().openInventory(new GeneratorListRandomBlock(plugin, generator).getInventory()));
        int slot = getSize() - 9;
        if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
            setItemWithAction(slot, new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                    .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhiZGM3YTFkNmNmZjc2YTkyNTU2NTJkMzE2NTUzMjI4NWFjYzNhOWQxYzBmMTJmMzljYTAwNzc2OWE3ZWExNCJ9fX0=")
                    .setName("§2Add Oraxen Block")
                    .addLore("§7Click to add random Oraxen block")
                    .build(),
                    event -> event.getWhoClicked().openInventory(new GeneratorAddRandomOraxen(plugin, generator).getInventory()));
            slot++;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            setItemWithAction(slot, new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                            .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhiZGM3YTFkNmNmZjc2YTkyNTU2NTJkMzE2NTUzMjI4NWFjYzNhOWQxYzBmMTJmMzljYTAwNzc2OWE3ZWExNCJ9fX0=")
                            .setName("§2Add ItemsAdder Block")
                            .addLore("§7Click to add random ItemsAdder block")
                            .build(),
                    event -> event.getWhoClicked().openInventory(new GeneratorAddRandomItemsAdder(plugin, generator).getInventory()));
        }
    }

    public static class DisplayableBlock {
        private final Material block;
        private final Material display;

        public DisplayableBlock(Material block, Material display) {
            this.block = block;
            this.display = display;
        }
    }
}
