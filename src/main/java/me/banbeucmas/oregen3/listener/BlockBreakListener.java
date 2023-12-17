package me.banbeucmas.oregen3.listener;

import com.cryptomorin.xseries.XBlock;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.util.BlockChecker;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private Oregen3 plugin;

    public BlockBreakListener(Oregen3 plugin) {
        this.plugin = plugin;
    }

    private int checkMode(Block block,
                              int waterBlock,
                              int lavaBlock,
                              int waterLava) {
        Block[] blocks =  new Block[BlockChecker.FACES.length];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = block.getRelative(BlockChecker.FACES[i]);
        }

        for (int i = 0; i < blocks.length; i++) {
            boolean blockIWater = XBlock.isWater(blocks[i].getType());
            boolean blockILava = XBlock.isLava(blocks[i].getType());
            //boolean blockIBlock = plugin.getBlockChecker().isBlock(blocks[i]);
            for (int j = 0; j < blocks.length; j++) {
                if (i == j) continue;

                //boolean blockJWater = XBlock.isWater(blocks[j].getType());
                boolean blockJLava = XBlock.isLava(blocks[j].getType());
                boolean blockJBlock = plugin.getBlockChecker().isBlock(blocks[j]);
                if (waterBlock >= 0 && blockIWater && blockJBlock) {
                    return waterBlock;
                }
                if (lavaBlock >= 0 && blockILava && blockJBlock) {
                    return lavaBlock;
                }
                if (waterLava >= 0 && blockIWater && blockJLava) {
                    return waterLava;
                }
            }
        }
        return -1;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        FileConfiguration config = plugin.getConfig();
        int waterBlock = config.getBoolean("mode.waterBlock") ?
                config.getInt("global.generators.check-regen.mode.waterBlock", -1) : -1;
        int lavaBlock = config.getBoolean("mode.lavaBlock") ?
                config.getInt("global.generators.check-regen.mode.lavaBlock", -1) : -1;
        int waterLava = config.getBoolean("mode.waterLava") ?
                config.getInt("global.generators.check-regen.mode.waterLava", -1) : -1;

        if (waterBlock < 0 && lavaBlock < 0 && waterLava < 0) return;

        Block block = event.getBlock();
        int delay = checkMode(block, waterBlock, lavaBlock, waterLava);
        if (delay == 0) {
            plugin.getBlockEventHandler().generateBlock(block);
        }
        else if (delay > 0) {
            plugin.getServer().getScheduler().runTaskLater(plugin,
                    () -> plugin.getBlockEventHandler().generateBlock(block), delay);
        }
    }
}
