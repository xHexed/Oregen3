package me.banbeucmas.oregen3.handler.event;

import me.banbeucmas.oregen3.Oregen3;
import org.bukkit.World;
import org.bukkit.block.Block;

public class AsyncBlockEventHandler extends BlockEventHandler {
    public AsyncBlockEventHandler(final Oregen3 plugin) {
        super(plugin);
    }

    @Override
    public void generateBlock(final World world, final Block block) {
        plugin.getUtils().runAsyncTask(() -> generate(world, block));
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
