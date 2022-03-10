package me.banbeucmas.oregen3.data.permission;

import org.bukkit.OfflinePlayer;

public interface PermissionChecker {
    /**
     * Check if the (offline) player has the provided permission or not
     *
     * @param world      the world to check (can be ignored), can be null
     * @param player     the (offline) player
     * @param permission the permission to check
     *
     * @return if the player has the permission or not
     */
    boolean checkPerm(final String world, final OfflinePlayer player, final String permission);
}
