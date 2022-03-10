package me.banbeucmas.oregen3.util;

import com.cryptomorin.xseries.XSound;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.data.Generator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;
import java.util.UUID;

import static me.banbeucmas.oregen3.Oregen3.getHook;
import static me.banbeucmas.oregen3.Oregen3.getPlugin;

public class PluginUtils {

    public static OfflinePlayer getOwner(final Location loc) {
        final UUID uuid = getHook().getIslandOwner(loc);
        if (uuid == null) {
            return null;
        }
        return Bukkit.getServer().getOfflinePlayer(uuid);
    }

    public static OfflinePlayer getOwner(final UUID uuid, World world) {
        final UUID p = getHook().getIslandOwner(uuid, world);
        if (p == null) {
            return null;
        }
        return Bukkit.getServer().getOfflinePlayer(p);
    }

    public static Generator getChosenGenerator(final Location loc) {
        Generator mc = DataManager.getChoosers().get(getPlugin().getConfig().getString("defaultGenerator", ""));
        switch (getPlugin().getConfig().getString("hooks.skyblock.getGeneratorMode", "owner")) {
            case "owner": {
                final OfflinePlayer p = getOwner(loc);
                if (p == null) {
                    break;
                }
                return getMaterialChooser(loc, mc, p);
            }
            case "lowest": {
                boolean ignore = getPlugin().getConfig().getBoolean("hooks.skyblock.ignoreOfflinePlayers", false);
                Generator lowestGen = null;
                for (final UUID uuid : getHook().getMembers(Objects.requireNonNull(getOwner(loc)).getUniqueId(), loc.getWorld())) {
                    final OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                    if (ignore && !p.isOnline()) continue;
                    final Generator chosen = getMaterialChooser(loc, mc, p);
                    if (lowestGen == null || lowestGen.getPriority() > chosen.getPriority()) {
                        lowestGen = chosen;
                    }
                }
                if (lowestGen == null) {
                    break;
                }
                return lowestGen;
            }
            case "highest": {
                boolean ignore = getPlugin().getConfig().getBoolean("hooks.skyblock.ignoreOfflinePlayers", false);
                Generator highestGen = null;
                for (final UUID uuid : getHook().getMembers(Objects.requireNonNull(getOwner(loc)).getUniqueId(), loc.getWorld())) {
                    final OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                    if (ignore && !p.isOnline()) continue;
                    final Generator chosen = getMaterialChooser(loc, mc, p);
                    if (highestGen == null || highestGen.getPriority() < chosen.getPriority()) {
                        highestGen = chosen;
                    }
                }
                if (highestGen == null) {
                    break;
                }
                return highestGen;
            }
        }
        return mc;
    }

    public static Generator getChosenGenerator(final UUID uuid, World world) {
        Generator mc = DataManager.getChoosers().get(getPlugin().getConfig().getString("defaultGenerator"));
        if (getPlugin().hasDependency()) {
            final UUID p = getHook().getIslandOwner(uuid, world);
            if (p == null) {
                return mc;
            }
            for (final Generator chooser : DataManager.getChoosers().values()) {
                if (Oregen3.getPermissionManager().checkPerm(world.getName(), Bukkit.getOfflinePlayer(p), chooser.getPermission())
                        && chooser.getPriority() >= mc.getPriority()
                        && getHook().getIslandLevel(p, null) >= chooser.getLevel()) {
                    mc = chooser;
                }
            }
        }
        return mc;
    }

    private static Generator getMaterialChooser(final Location loc, Generator mc, final OfflinePlayer p) {
        final double level = getHook().getIslandLevel(p.getUniqueId(), loc);
        for (final Generator chooser : DataManager.getChoosers().values()) {
            if (Oregen3.getPermissionManager().checkPerm(null, p, chooser.getPermission())
                    && chooser.getPriority() >= mc.getPriority()
                    && level >= chooser.getLevel()) {
                mc = chooser;
            }
        }
        return mc;
    }

    public static void sendBlockEffect(final World world, final Block to, final Oregen3 plugin, final Generator mc) {
        if (mc.isSoundEnabled())
            world.playSound(to.getLocation(), mc.getSound(), mc.getSoundVolume(), mc.getSoundPitch());
        else if (plugin.getConfig().getBoolean("global.generators.sound.enabled", false)) {
            world.playSound(to.getLocation(),
                    Objects.requireNonNull(XSound.matchXSound(plugin.getConfig().getString("global.generators.sound.name", "BLOCK_FIRE_EXTINGUISH")).map(XSound::parseSound).orElse(XSound.BLOCK_FIRE_EXTINGUISH.parseSound())),
                    (float) plugin.getConfig().getDouble("global.generators.sound.volume", 1),
                    (float) plugin.getConfig().getDouble("global.generators.sound.pitch", 1)
            );
        }
    }
}
