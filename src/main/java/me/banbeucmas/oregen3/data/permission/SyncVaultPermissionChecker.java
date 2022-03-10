package me.banbeucmas.oregen3.data.permission;

import me.banbeucmas.oregen3.Oregen3;
import org.bukkit.OfflinePlayer;

public class SyncVaultPermissionChecker implements PermissionChecker {
    @Override
    public boolean checkPerm(final String world, final OfflinePlayer player, final String permission) {
        if (player.isOnline()) {
            return Oregen3.getPerm().playerHas(player.getPlayer(), permission);
        }
        else {
            return Oregen3.getPerm().playerHas(world, player, permission);
        }
    }
}
