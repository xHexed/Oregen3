package me.banbeucmas.oregen3.util;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XTag;
import me.banbeucmas.oregen3.Oregen3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockChecker {
    public static final BlockFace[] FACES = {
            BlockFace.SELF,
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
    };

    private Oregen3 plugin;

    public BlockChecker(Oregen3 plugin) {
        this.plugin = plugin;
    }

    public boolean isBlock(final Block b) {
        return XTag.anyMatchString(b.getType().name(), plugin.getConfig().getStringList("blocks"));
    }

    public static boolean isSurroundedByWater(final Location loc) {
        final World world = loc.getWorld();
        final int x = loc.getBlockX();
        final int y = loc.getBlockY();
        final int z = loc.getBlockZ();
        return XBlock.isWater(world.getBlockAt(x + 1, y, z).getType()) ||
                XBlock.isWater(world.getBlockAt(x - 1, y, z).getType()) ||
                XBlock.isWater(world.getBlockAt(x, y, z + 1).getType()) ||
                XBlock.isWater(world.getBlockAt(x, y, z - 1).getType());
    }
}
