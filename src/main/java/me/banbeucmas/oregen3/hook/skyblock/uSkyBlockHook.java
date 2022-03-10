package me.banbeucmas.oregen3.hook.skyblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import us.talabrek.ultimateskyblock.api.IslandInfo;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// "Stupid api only use for online players rip" - xHexed, 4/7/2020
public class uSkyBlockHook implements SkyblockHook {
    private final uSkyBlockAPI usb;

    public uSkyBlockHook() {
        usb = (uSkyBlockAPI) Bukkit.getPluginManager().getPlugin("uSkyBlock");
    }

    @Override
    public double getIslandLevel(final UUID uuid, final Location loc) {
        final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (!player.isOnline()) return 0;
        return (long) usb.getIslandLevel(player.getPlayer());
    }

    @SuppressWarnings("deprecation")
    @Override
    public UUID getIslandOwner(final Location loc) {
        final IslandInfo info = usb.getIslandInfo(loc);
        // We don't have other choice but to use the deprecated method because the api only return the leader's name :(
        return info == null ? null : Bukkit.getOfflinePlayer(usb.getIslandInfo(loc).getLeader()).getUniqueId();
    }

    @Override
    public UUID getIslandOwner(final UUID uuid, World world) {
        // Couldn't find any method related to this...
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<UUID> getMembers(final UUID uuid, World world) {
        final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (!player.isOnline()) return null;
        final List<UUID> list = new ArrayList<>();
        for (final String s : usb.getIslandInfo(player.getPlayer()).getMembers()) {
            list.add(Bukkit.getOfflinePlayer(s).getUniqueId());
        }
        return list;
    }
}
