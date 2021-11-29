package me.banbeucmas.oregen3.hook.blockplacer;

import com.cryptomorin.xseries.XMaterial;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;
import me.banbeucmas.oregen3.handler.block.placer.BlockPlacer;
import org.bukkit.block.Block;

public class OraxenBlockPlacer implements BlockPlacer {
    String itemID;

    public OraxenBlockPlacer(String block) {
        itemID = block.substring(block.indexOf('-') + 1);
    }

    @Override
    public void placeBlock(Block block) {
        block.setType(XMaterial.NOTE_BLOCK.parseMaterial());
        NoteBlockMechanicFactory.setBlockModel(block, itemID);
    }
}
