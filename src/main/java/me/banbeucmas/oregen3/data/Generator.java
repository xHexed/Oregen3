package me.banbeucmas.oregen3.data;

import com.cryptomorin.xseries.XSound;
import lombok.AccessLevel;
import lombok.Getter;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.handler.block.placer.BlockPlacer;
import me.banbeucmas.oregen3.handler.block.placer.VanillaBlockPlacer;
import me.banbeucmas.oregen3.hook.blockplacer.OraxenBlockPlacer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Generator {
    private String id;
    private long priority;
    private double level;
    private String permission;
    private String name;
    private boolean soundEnabled;
    private Sound sound;
    private float soundVolume;
    private float soundPitch;
    private boolean worldEnabled;
    private boolean worldBlacklist;
    private Set<String> worldList;
    private Map<String, Double> random;
    private transient double totalChance;
    @Getter(value = AccessLevel.NONE)
    private transient BlockPlacer[] blockPlacers;
    @Getter(value = AccessLevel.NONE)
    private transient Double[] chances;

    Generator(final String id, Oregen3 plugin) {
        this.id = id;
        final ConfigurationSection path = plugin.getConfig().getConfigurationSection("generators." + id);

        name = Objects.requireNonNull(path).getString("name", id);
        permission = path.getString("permission", "oregen3.generator." + id);
        priority   = path.getLong("priority", 0);
        level      = path.getDouble("level", 0);
        if (path.isSet("sound")) {
            soundEnabled = true;
            sound        = XSound.matchXSound(path.getString("sound.name", "BLOCK_FIRE_EXTINGUISH")).map(XSound::parseSound).orElse(XSound.BLOCK_FIRE_EXTINGUISH.parseSound());
            soundVolume  = (float) path.getDouble("sound.volume", 1);
            soundPitch   = (float) path.getDouble("sound.pitch", 1);
        }
        if (path.isSet("world")) {
            worldEnabled   = true;
            worldBlacklist = path.getBoolean("world.blacklist", true);
            worldList      = new HashSet<>(path.getStringList("world.list"));
        }
        final Set<String> randomList = Objects.requireNonNull(path.getConfigurationSection("random")).getKeys(false);
        random = new HashMap<>();
        for (final String mat : randomList) {
            random.put(mat, path.getDouble("random" + mat));
        }

        init();
    }

    public void init() {
        chances = new Double[random.size()];
        blockPlacers = new BlockPlacer[random.size()];
        int i = 0;
        for (Map.Entry<String, Double> entry : random.entrySet()) {
            String mat = entry.getKey();
            Double chance = entry.getValue();
            if (mat.startsWith("oraxen-")) {
                blockPlacers[i] = new OraxenBlockPlacer(mat);
            } else {
                blockPlacers[i] = new VanillaBlockPlacer(mat);
            }
            totalChance += chance;
            chances[i] = totalChance;
            i++;
        }
    }

    public BlockPlacer randomChance() {
        final Double chance = ThreadLocalRandom.current().nextDouble(getTotalChance());
        int chosenBlock = Arrays.binarySearch(chances, chance);
        if (chosenBlock < 0) {
            chosenBlock = -(chosenBlock + 1);
        }
        return blockPlacers[chosenBlock];
    }
}
