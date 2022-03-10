package me.banbeucmas.oregen3.handler.event;

import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.util.PluginUtils;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class BlockEventHandler {
    public abstract void generateBlock(final World world, final Block source, final Block to);

    void generate(final World world, final Block source, final Block to) {
        final Generator mc = PluginUtils.getChosenGenerator(source.getLocation());
        if (mc.isWorldEnabled() && mc.getWorldList().contains(to.getWorld().getName()) == mc.isWorldBlacklist())
            return;
        Oregen3.getBlockPlaceHandler().placeBlock(to, mc.randomChance());
        PluginUtils.sendBlockEffect(world, to, Oregen3.getPlugin(), mc);
    }

    public abstract boolean isAsync();
}
