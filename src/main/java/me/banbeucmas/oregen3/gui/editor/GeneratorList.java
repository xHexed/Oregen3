package me.banbeucmas.oregen3.gui.editor;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.gui.GUICommons;
import me.banbeucmas.oregen3.gui.PagedInventory;
import me.banbeucmas.oregen3.util.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GeneratorList extends PagedInventory<Generator> {
    private Oregen3 plugin;
    public GeneratorList(Oregen3 plugin) {
        this(plugin, 1);
    }

    public GeneratorList(Oregen3 plugin, int page) {
        super(new ArrayList<>(plugin.getDataManager().getGenerators().values()), "Generators [page %page%]", page);
        this.plugin = plugin;
    }

    @Override
    public ItemStack getContentIcon(Generator generator) {
        return GUICommons.createGeneratorIcon(generator).addLore("", "§eClick to edit").build();
    }

    @Override
    public void getContentAction(Generator generator, InventoryClickEvent event) {
        event.getWhoClicked().openInventory(new GeneratorMenu(plugin, generator).getInventory());
    }

    @Override
    public void addOtherIcons() {
        super.setItemWithAction(getSize() - 9, new ItemBuilder(XMaterial.FURNACE.parseItem())
                .setName("§eCreate new")
                .addLore("§7Add a new generator")
                .build(), (event) -> {
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage("Type name of new generator, or cancel");
            plugin.getChatListener().addChatAction(event.getWhoClicked(), (name) -> {
                if (name.equals("cancel")) {
                    event.getWhoClicked().sendMessage("Cancelled");
                }
                else {
                    plugin.getConfigManager().setConfig((config) ->
                            config.set("generators." + name + ".random.COBBLESTONE", 100));
                    plugin.getDataManager().loadData();
                }
                event.getWhoClicked().openInventory(new GeneratorList(plugin, getPage()).getInventory());
            });
        });
        super.setItemWithAction(getSize() - 1, GUICommons.GO_BACK_ICON,
                (event) -> event.getWhoClicked().openInventory(new EditorGUI(plugin).getInventory()));
    }
}
