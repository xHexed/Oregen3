package me.banbeucmas.oregen3.managers.items;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public enum SkullIndex {

    GENERATOR("7f9f356f5fe7d1bc92cddfaeba3ee773ac9df1cc4d1c2f8fe5f47013032c551d"),
    PREVIOUS("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9"),
    NEXT("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf");

    private String id;
    private GameProfile profile;
    private ItemStack stack;

    SkullIndex() {
        this("");
    }

    SkullIndex(String id) {
        this.id = id;
        this.stack = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial());

        SkullMeta meta = (SkullMeta) this.stack.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), this.name());
        profile.getProperties().clear();
        PropertyMap map = profile.getProperties();
        byte[] encoded = Base64.encodeBase64(String.format("{\"textures\":{\"SKIN\":" +
                "{\"url\":\"http://textures.minecraft.net/texture/%s\"}}}", this.id).getBytes());
        map.put("textures", new Property("textures", new String(encoded)));

        this.profile = profile;

        try {
            assert meta != null;
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        this.stack.setItemMeta(meta);
    }

    public GameProfile getProfile() {
        return profile;
    }

    public String getId() {
        return id;
    }

    public ItemStack asItem() {
        return stack;
    }
}