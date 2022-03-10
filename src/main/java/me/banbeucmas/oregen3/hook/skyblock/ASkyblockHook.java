package me.banbeucmas.oregen3.hook.skyblock;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.UUID;

public class ASkyblockHook implements SkyblockHook {
    private final ASkyBlockAPI api;

    public ASkyblockHook() {
        api = ASkyBlockAPI.getInstance();
    }

    @Override
    public double getIslandLevel(final UUID uuid, final Location loc) {
        return api.getLongIslandLevel(uuid);
    }

    @Override
    public UUID getIslandOwner(final Location loc) {
        final Island island = api.getIslandAt(loc);
        return island == null ? null : island.getOwner();
    }

    @Override
    public UUID getIslandOwner(final UUID uuid, World world) {
        return api.getTeamLeader(uuid);
    }

    @Override
    public List<UUID> getMembers(final UUID uuid, World world) {
        return api.getIslandOwnedBy(uuid).getMembers();
    }
}
