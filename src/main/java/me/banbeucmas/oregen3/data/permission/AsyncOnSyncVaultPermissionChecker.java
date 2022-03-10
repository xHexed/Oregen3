package me.banbeucmas.oregen3.data.permission;

import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AsyncOnSyncVaultPermissionChecker implements PermissionChecker, Listener {
    private final Oregen3 plugin;
    private final Map<String, HashSet<String>> permissionList = new HashMap<>();

    public AsyncOnSyncVaultPermissionChecker(final Oregen3 plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean checkPerm(final String world, final OfflinePlayer player, final String permission) {
        checkContains(player.getName());
        final HashSet<String> list = permissionList.get(player.getName());

        if (player.isOnline()) {
            if (!list.contains(permission) && Oregen3.getPerm().playerHas(player.getPlayer(), permission))
                list.add(permission);
            else if (list.contains(permission) && !Oregen3.getPerm().playerHas(player.getPlayer(), permission))
                list.remove(permission);
        }
        else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!list.contains(permission) && Oregen3.getPerm().playerHas(world, player, permission))
                        list.add(permission);
                    else if (list.contains(permission) && !Oregen3.getPerm().playerHas(world, player, permission))
                        list.remove(permission);
                }
            }.runTaskAsynchronously(plugin);
        }

        return list.contains(permission);
    }

    private void checkPerms(final OfflinePlayer player) {
        checkContains(player.getName());
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final Generator chooser : DataManager.getChoosers().values()) {
                    checkPerm(null, player, chooser.getPermission());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void checkContains(final String player) {
        if (!permissionList.containsKey(player)) {
            permissionList.put(player, new HashSet<>());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlock(final BlockDamageEvent e) {
        final OfflinePlayer player = PluginUtils.getOwner(e.getPlayer().getUniqueId(), e.getPlayer().getWorld());
        if (player == null) return;
        checkPerms(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent e) {
        final OfflinePlayer player = PluginUtils.getOwner(e.getPlayer().getUniqueId(), e.getPlayer().getWorld());
        if (player == null) return;
        checkPerms(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent e) {
        final OfflinePlayer player = PluginUtils.getOwner(e.getPlayer().getUniqueId(), e.getPlayer().getWorld());
        if (player == null) return;
        checkPerms(player);
    }
}
