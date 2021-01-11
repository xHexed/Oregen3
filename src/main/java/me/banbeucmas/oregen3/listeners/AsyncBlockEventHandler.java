package me.banbeucmas.oregen3.listeners;

import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

public class AsyncBlockEventHandler implements BlockEventHandler {
    @Override
    public void generateBlock(final World world, final Block source, final Block to, final FileConfiguration config) {
        Bukkit.getScheduler().runTaskAsynchronously(Oregen3.getPlugin(), () -> {
            final Generator mc = PluginUtils.getChooser(source.getLocation());
            if (mc.isWorldEnabled() && mc.getWorldList().contains(to.getWorld().getName()) == mc.isWorldBlacklist())
                return;
            BlockListener.sendBlockEffect(world, to, config, mc);
            final Material randomMaterial = BlockListener.randomChance(mc);
            Bukkit.getScheduler().runTask(Oregen3.getPlugin(), () -> to.setType(randomMaterial));
        });
    }

}