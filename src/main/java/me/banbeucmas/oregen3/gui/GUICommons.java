package me.banbeucmas.oregen3.gui;

import com.cryptomorin.xseries.XMaterial;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.util.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Map;

public class GUICommons {
    public static final ItemStack BORDER_ICON = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.get()).setName("§0").build();
    public static final ItemStack GO_BACK_ICON = new ItemBuilder(XMaterial.ARROW.get()).setName("§e<- Go Back ").build();

    public static final ItemStack NEXT_PAGE_ICON = new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
            .setName("§eNext Page ->")
            .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTAyZmEzYjJkY2IxMWM2NjM5Y2M5YjkxNDZiZWE1NGZiYzY2NDZkODU1YmRkZTFkYzY0MzUxMjRhMTEyMTVkIn19fQ==")
            .build();
    public static final ItemStack PREVIOUS_PAGE_ICON = new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
            .setName("§e<- Previous Page")
            .setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQxMzNmNmFjM2JlMmUyNDk5YTc4NGVmYWRjZmZmZWI5YWNlMDI1YzM2NDZhZGE2N2YzNDE0ZTVlZjMzOTQifX19")
            .build();

    public static ItemBuilder createGeneratorIcon(Generator generator) {
        Map<String, Double> random = generator.getRandom();
        ItemBuilder item = new ItemBuilder(XMaterial.FURNACE.parseItem());
        item.setName("§7Generator §6" + generator.getId());
        item.addLore("",
                "§7Name: " + generator.getName(),
                "§7Permission: " + generator.getPermission(),
                "§7Priority: " + generator.getPriority(),
                "§7Level: " + generator.getLevel(),
                "",
                "§7Random:");
        Iterator<Map.Entry<String, Double>> iterator = random.entrySet().iterator();
        for (int mc = 0; mc < Math.min(10, random.size()); mc++) {
            Map.Entry<String, Double> entry = iterator.next();
            item.addLore("§6 ● §8" + entry.getKey() + ":§e " + entry.getValue() + "%");
        }
        if (random.size() >= 10) item.addLore("§6 ● §8And §e" + (random.size() - 9) + " §8other block(s)");
        return item;
    }
}
