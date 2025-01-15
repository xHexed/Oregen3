package me.banbeucmas.oregen3.gui.editor.generator;

import com.cryptomorin.xseries.XMaterial;
import io.th0rgal.oraxen.api.OraxenBlocks;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.GUICommons;
import me.banbeucmas.oregen3.gui.PagedInventory;
import me.banbeucmas.oregen3.util.ItemBuilder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GeneratorAddRandomOraxen extends PagedInventory<String> {
    private Oregen3 plugin;
    private Generator generator;

    public GeneratorAddRandomOraxen(Oregen3 plugin, Generator generator) {
        this(plugin, generator, 1);
    }

    public GeneratorAddRandomOraxen(Oregen3 plugin, Generator generator, int page) {
        super(new ArrayList<>(OraxenBlocks.getBlockIDs()), "Oraxen [page %page%]", page);
        this.plugin = plugin;
        this.generator = generator;
    }

    @Override
    public ItemStack getContentIcon(String item) {
        return new ItemBuilder(XMaterial.NOTE_BLOCK.get()).addLore("§eClick to add block").build();
    }

    @Override
    public void getContentAction(String material, InventoryClickEvent event) {
        if (event.isLeftClick()) {
            addBlock(material, event, 1.0);
        } else if (event.isRightClick()) {
            HumanEntity player = event.getWhoClicked();
            player.sendMessage("Type chance of new block " + material + "in generator " + generator.getId());
            plugin.getChatListener().addChatAction(player, (chance) -> {
                if (chance.equals("cancel")) {
                    player.sendMessage("Cancelled");
                } else {
                    try {
                        addBlock(material, event, Double.parseDouble(chance));
                    } catch (NumberFormatException ex) {
                        player.sendMessage(chance + " is not a number...");
                    }
                }
                event.getWhoClicked().openInventory(new GeneratorAddRandomOraxen(plugin, generator, getPage()).getInventory());
            });
        }
    }

    private void addBlock(String material, InventoryClickEvent event, double chance) {
        plugin.getConfigManager().setConfig(config -> config.set("generators." + generator.getId() + ".random.oraxen-" + material, chance));
        generator.addRandomBlock(material, chance);
        event.getWhoClicked().openInventory(new GeneratorListRandomBlock(plugin, generator).getInventory());
    }

    @Override
    public void addOtherIcons() {
        setItemWithAction(getSize() - 1, GUICommons.GO_BACK_ICON,
                (event) -> event.getWhoClicked().openInventory(new GeneratorListRandomBlock(plugin, generator).getInventory()));
    }
}
