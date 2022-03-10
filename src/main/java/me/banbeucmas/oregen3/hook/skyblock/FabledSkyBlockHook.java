package me.banbeucmas.oregen3.hook.skyblock;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FabledSkyBlockHook implements SkyblockHook {
    private final IslandManager manager;

    public FabledSkyBlockHook() {
        manager = SkyBlock.getInstance().getIslandManager();
    }

    @Override
    public double getIslandLevel(final UUID uuid, final Location loc) {
        final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        final Island is = manager.getIsland(player);
        if (is == null || is.getLevel() == null) return 0;
        return is.getLevel().getLevel();
    }

    @Override
    public UUID getIslandOwner(final Location loc) {
        final Island island = manager.getIslandAtLocation(loc);
        return island == null ? null : island.getOwnerUUID();
    }

    @Override
    public UUID getIslandOwner(final UUID uuid, World world) {
        return manager.getIsland(Bukkit.getOfflinePlayer(uuid)).getOwnerUUID();
    }

    @Override
    public List<UUID> getMembers(final UUID uuid, World world) {
        List<UUID> members = new ArrayList<>();
        manager.getPlayersAtIsland(manager.getIslandByUUID(uuid)).forEach((player -> members.add(player.getUniqueId())));
        return members;
    }
}
