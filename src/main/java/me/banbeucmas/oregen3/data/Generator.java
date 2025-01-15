package me.banbeucmas.oregen3.data;

import com.cryptomorin.xseries.XSound;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.handler.block.placer.BlockPlacer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Data
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
    @Getter(value = AccessLevel.NONE) @Setter(value = AccessLevel.NONE)
    private transient BlockPlacer[] blockPlacers;
    @Getter(value = AccessLevel.NONE) @Setter(value = AccessLevel.NONE)
    private transient Double[] chances;

    Generator(Oregen3 plugin, final String id) {
        this.id = id;
        final ConfigurationSection path = plugin.getConfig().getConfigurationSection("generators." + id);

        name = Objects.requireNonNull(path).getString("name", id);
        permission = path.getString("permission", "oregen3.generator." + id);
        priority   = path.getLong("priority", 0);
        level      = path.getDouble("level", 0);
        if (path.isSet("sound")) {
            soundEnabled = path.getBoolean("sound.enabled", true);
            sound        = XSound.of(path.getString("sound.name", "BLOCK_FIRE_EXTINGUISH")).map(XSound::get).orElse(XSound.BLOCK_FIRE_EXTINGUISH.get());
            soundVolume  = (float) path.getDouble("sound.volume", 1);
            soundPitch   = (float) path.getDouble("sound.pitch", 1);
        }
        if (path.isSet("world")) {
            worldEnabled   = true;
            worldBlacklist = path.getBoolean("world.blacklist", true);
            worldList      = new LinkedHashSet<>(path.getStringList("world.list"));
        }
        random = new LinkedHashMap<>();
        for (final String mat : Objects.requireNonNull(path.getConfigurationSection("random")).getKeys(false)) {
            random.put(mat, path.getDouble("random." + mat));
        }

        chances = new Double[random.size()];
        blockPlacers = new BlockPlacer[random.size()];
        setRandomChance();
    }

    public void addRandomBlock(String mat, double chance) {
        random.put(mat, chance);
        chances = Arrays.copyOf(chances, chances.length + 1);
        blockPlacers = Arrays.copyOf(blockPlacers, blockPlacers.length + 1);

        blockPlacers[blockPlacers.length - 1] = BlockPlacer.getBlockPlacer(mat);
        totalChance += chance;
        chances[chances.length - 1] = totalChance;
    }

    public void setRandomBlock(String mat, double chance) {
        random.put(mat, chance);
        setRandomChance();
    }

    public void removeRandomBlock(String mat) {
        random.remove(mat);
        chances = Arrays.copyOf(chances, chances.length - 1);
        blockPlacers = Arrays.copyOf(blockPlacers, blockPlacers.length - 1);
        setRandomChance();
    }

    private void setRandomChance() {
        int i = 0;
        totalChance = 0;
        for (Map.Entry<String, Double> entry : random.entrySet()) {
            String mat = entry.getKey();
            Double chance = entry.getValue();
            blockPlacers[i] = BlockPlacer.getBlockPlacer(mat);
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
