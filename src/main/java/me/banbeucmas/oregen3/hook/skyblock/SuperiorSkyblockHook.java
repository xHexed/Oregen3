package me.banbeucmas.oregen3.hook.skyblock;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SuperiorSkyblockHook implements SkyblockHook {

    @Override
    public double getIslandLevel(final UUID uuid, final Location loc) {
        return SuperiorSkyblockAPI.getPlayer(uuid).getIsland().getIslandLevel().doubleValue();
    }

    @Override
    public UUID getIslandOwner(final Location loc) {
        final Island island = SuperiorSkyblockAPI.getIslandAt(loc);
        return island == null ? null : island.getOwner().getUniqueId();
    }

    @Override
    public UUID getIslandOwner(final UUID uuid, World world) {
        return SuperiorSkyblockAPI.getPlayer(uuid).getIslandLeader().getUniqueId();
    }

    @Override
    public List<UUID> getMembers(final UUID uuid, World world) {
        final List<UUID> list = new ArrayList<>();
        for (final SuperiorPlayer superiorPlayer : SuperiorSkyblockAPI.getPlayer(uuid).getIsland().getIslandMembers(true)) {
            list.add(superiorPlayer.getUniqueId());
        }
        return list;
    }
}
