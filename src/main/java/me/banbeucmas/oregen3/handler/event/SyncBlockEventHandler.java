package me.banbeucmas.oregen3.handler.event;

import me.banbeucmas.oregen3.Oregen3;
import org.bukkit.block.Block;

public class SyncBlockEventHandler extends BlockEventHandler {
    public SyncBlockEventHandler(Oregen3 plugin) {
        super(plugin);
    }

    @Override
    public void generateBlock(final Block block) {
        generate(block);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
