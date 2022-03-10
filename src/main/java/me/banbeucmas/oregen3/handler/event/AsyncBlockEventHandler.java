package me.banbeucmas.oregen3.handler.event;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class AsyncBlockEventHandler extends BlockEventHandler {
    private final Plugin plugin;

    public AsyncBlockEventHandler(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void generateBlock(final World world, final Block source, final Block to) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> generate(world, source, to));
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
