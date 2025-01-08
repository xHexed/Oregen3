package me.banbeucmas.oregen3.manager;

import com.bgsoftware.common.config.CommentedConfiguration;
import me.banbeucmas.oregen3.Oregen3;

import java.io.IOException;
import java.util.function.Consumer;

public class ConfigManager {
    private Oregen3 plugin;

    public ConfigManager(Oregen3 plugin) {
        this.plugin = plugin;
    }

    public void setConfig(Consumer<CommentedConfiguration> action) {
        CommentedConfiguration config = CommentedConfiguration.loadConfiguration(plugin.getConfigFolder());
        if (!plugin.getConfigFolder().exists())
            plugin.saveResource("config.yml", false);
        action.accept(config);
        try {
            config.save(plugin.getConfigFolder());
            plugin.reloadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
