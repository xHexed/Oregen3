package me.banbeucmas.oregen3.listener;

import com.cryptomorin.xseries.XBlock;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.util.BlockChecker;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class BlockListener implements Listener {
    private Oregen3 plugin;
    private final FileConfiguration config;

    public BlockListener(final Oregen3 plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    private boolean canGenerateBlock(Block src,
                                     Block to,
                                     boolean waterBlock,
                                     boolean lavaBlock) {
        final Material material = src.getType();
        for (final BlockFace face : BlockChecker.FACES) {
            final Block check = to.getRelative(face);
            if (plugin.getBlockChecker().isBlock(check)
                    && (XBlock.isWater(material))
                    && waterBlock) {
                return true;
            }
            else if (plugin.getBlockChecker().isBlock(check)
                    && (XBlock.isLava(material))
                    && lavaBlock) {
                return true;
            }
        }
        return false;
    }

    /*
    Checks for Water + Lava, block will use another method to prevent confusion
     */
    private boolean canGenerate(final Material material, final Block b) {
        final boolean check = XBlock.isWater(material);
        for (final BlockFace face : BlockChecker.FACES) {
            final Material type = b.getRelative(face).getType();
            if (((check && XBlock.isLava(type)) || (!check && XBlock.isWater(type)))) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onOre(final BlockFromToEvent event) {
        final Block source = event.getBlock();
        final Block to = event.getToBlock();
        final Material sourceMaterial = source.getType();
        final Material toMaterial = to.getType();

        if (XBlock.isAir(sourceMaterial))
            return;

        if (config.getBoolean("global.generators.world.enabled", false)
                && config.getBoolean("global.generators.world.blacklist", true)
                == config.getStringList("global.generators.world.list").contains(to.getWorld().getName())) {
            return;
        }

        if (XBlock.isWater(sourceMaterial) || XBlock.isLava(sourceMaterial)) {
            if ((XBlock.isAir(toMaterial) || XBlock.isWater(toMaterial))
                    && XBlock.isWaterStationary(source)
                    && config.getBoolean("mode.waterLava")
                    && canGenerate(sourceMaterial, to)
                    && event.getFace() != BlockFace.DOWN) {
                if (XBlock.isLava(sourceMaterial) && !BlockChecker.isSurroundedByWater(to.getLocation())) {
                    return;
                }
                event.setCancelled(true);
                plugin.getBlockEventHandler().generateBlock(to);
            }
            else if (canGenerateBlock(source,
                    to,
                    config.getBoolean("mode.waterBlock"),
                    config.getBoolean("mode.lavaBlock"))) {
                event.setCancelled(true);
                plugin.getBlockEventHandler().generateBlock(to);
            }
        }
    }
}

