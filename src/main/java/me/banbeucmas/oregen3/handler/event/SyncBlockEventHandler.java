package me.banbeucmas.oregen3.handler.event;

import org.bukkit.World;
import org.bukkit.block.Block;

public class SyncBlockEventHandler extends BlockEventHandler {
    @Override
    public void generateBlock(final World world, final Block source, final Block to) {
        generate(world, source, to);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
