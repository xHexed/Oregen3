package me.banbeucmas.oregen3.hook.skyblock;

import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.request.AddonRequestBuilder;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BentoBoxHook implements SkyblockHook {
    private IslandsManager manager;
    private boolean level = true;

    public BentoBoxHook() {
        Bukkit.getScheduler().runTaskLater(Oregen3.getPlugin(), () -> {
            manager = BentoBox.getInstance().getIslands();

            if (!BentoBox.getInstance().getAddonsManager().getAddonByName("Level").isPresent()) {
                Oregen3.getPlugin().getLogger().warning(StringUtils.getPrefixString("Level addon for BentoBox not found! Turning island level feature off...", null));
                level = false;
            }
        }, 2);
    }

    @Override
    public double getIslandLevel(final UUID uuid, final Location loc) {
        double num = 0;
        if (level) {
            final AddonRequestBuilder builder = new AddonRequestBuilder()
                    .addon("Level")
                    .label("island-level")
                    .addMetaData("player", uuid);
            if (loc != null) {
                num = ((Long) builder
                        .addMetaData("world-name", loc.getWorld())
                        .request()).doubleValue();
            }
            return num;
        }
        else
            return 0;
    }

    @Override
    public UUID getIslandOwner(final Location loc) {
        final Optional<Island> island = manager.getIslandAt(loc);
        return island.map(Island::getOwner).orElse(null);
    }

    @Override
    public UUID getIslandOwner(final UUID uuid, World world) {
        return manager.getOwner(world, uuid);
    }

    @Override
    public List<UUID> getMembers(final UUID uuid, World world) {
        final Island island = manager.getIsland(world, uuid);
        if (island == null) return null;
        return new ArrayList<>(island.getMembers().keySet());
    }

    @Override
    public boolean isIslandWorldSingle() {
        return false;
    }
}
