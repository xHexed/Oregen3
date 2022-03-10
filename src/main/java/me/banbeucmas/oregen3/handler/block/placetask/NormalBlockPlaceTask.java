package me.banbeucmas.oregen3.handler.block.placetask;

import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.handler.block.placer.BlockPlacer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public class NormalBlockPlaceTask implements BlockPlaceTask {
    private Oregen3 plugin;
    private BlockPlaceTask placeTask;

    public NormalBlockPlaceTask(Oregen3 plugin) {
        this.plugin = plugin;
        if (plugin.getEventHandler().isAsync()) {
            placeTask = new SyncBlockPlaceTask();
        }
        else {
            placeTask = new DefaultBlockPlaceTask();
        }
    }

    @Override
    public void placeBlock(Block block, BlockPlacer placer) {
        placeTask.placeBlock(block, placer);
    }

    @Override
    public void stop() { }

    private class SyncBlockPlaceTask implements BlockPlaceTask {
        @Override
        public void placeBlock(Block block, BlockPlacer blockPlacer) {
            Bukkit.getScheduler().runTask(plugin, () -> blockPlacer.placeBlock(block));
        }

        @Override
        public void stop() { }
    }

    private static class DefaultBlockPlaceTask implements BlockPlaceTask {
        @Override
        public void placeBlock(Block block, BlockPlacer blockPlacer) {
            blockPlacer.placeBlock(block);
        }

        @Override
        public void stop() { }
    }
}
