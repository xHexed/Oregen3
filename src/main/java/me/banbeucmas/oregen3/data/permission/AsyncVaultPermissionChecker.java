package me.banbeucmas.oregen3.data.permission;

import me.banbeucmas.oregen3.Oregen3;
import org.bukkit.OfflinePlayer;

public class AsyncVaultPermissionChecker implements PermissionChecker {
    @Override
    public boolean checkPerm(String world, OfflinePlayer player, String permission) {
        return Oregen3.getPerm().playerHas(world, player, permission);
    }
}
