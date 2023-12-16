package me.banbeucmas.oregen3.handler.event;

import com.cryptomorin.xseries.XSound;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.data.Generator;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;

public abstract class BlockEventHandler {
    Oregen3 plugin;

    public BlockEventHandler(Oregen3 plugin) {
        this.plugin = plugin;
    }

    public abstract void generateBlock(final Block block);

    void generate(final Block block) {
        final Generator mc = plugin.getUtils().getChosenGenerator(block.getLocation());
        if (mc == null) return;
        plugin.getBlockPlaceTask().placeBlock(block, mc.randomChance());
        sendBlockEffect(block.getWorld(), block, mc);
    }

    private void sendBlockEffect(final World world, final Block to, final Generator mc) {
        if (mc.isSoundEnabled())
            world.playSound(to.getLocation(), mc.getSound(), mc.getSoundVolume(), mc.getSoundPitch());
        else if (plugin.getConfig().getBoolean("global.generators.sound.enabled", false)) {
            world.playSound(to.getLocation(),
                    Objects.requireNonNull(XSound.matchXSound(plugin.getConfig().getString("global.generators.sound.name", "BLOCK_FIRE_EXTINGUISH")).map(XSound::parseSound).orElse(XSound.BLOCK_FIRE_EXTINGUISH.parseSound())),
                    (float) plugin.getConfig().getDouble("global.generators.sound.volume", 1),
                    (float) plugin.getConfig().getDouble("global.generators.sound.pitch", 1)
            );
        }
    }

    public abstract boolean isAsync();
}
