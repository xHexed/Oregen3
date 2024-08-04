package me.banbeucmas.oregen3.util;

import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private ItemStack stack;
    private Material type;
    private String name;
    private List<String> lore = new ArrayList<>();
    private byte data = 0;
    private String skull;

    public ItemBuilder(Material type) {
        this.type = type;
    }

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
    }

    public ItemBuilder setData(byte data) {
        this.data = data;
        return this;
    }

    public ItemBuilder setName(String name) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        return this;
    }

    public ItemBuilder setName(String name, ChatColor color) {
        this.name = color + name;
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        for (String s : lore) {
            this.lore.add(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY + s));
        }
        return this;
    }

    public ItemBuilder setSkull(String skull) {
        this.skull = skull;
        return this;
    }

    public ItemStack build() {
        int amount = 1;
        ItemStack item = stack == null ? new ItemStack(this.type, amount, this.data) : stack;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(name);
        meta.setLore(lore);
        if (this.data > 0) {
            item.setDurability(this.data);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
        if (meta instanceof SkullMeta) {
            String skull = this.skull;
            if (skull != null) XSkull.of(meta).profile(Profileable.detect(skull)).apply();
        }
        item.setItemMeta(meta);
        return item;
    }

}
