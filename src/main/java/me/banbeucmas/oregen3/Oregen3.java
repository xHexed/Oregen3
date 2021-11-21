package me.banbeucmas.oregen3;

import me.banbeucmas.oregen3.commands.CommandHandler;
import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.data.permission.AsyncVaultPermission;
import me.banbeucmas.oregen3.data.permission.DefaultPermission;
import me.banbeucmas.oregen3.data.permission.PermissionManager;
import me.banbeucmas.oregen3.data.permission.VaultPermission;
import me.banbeucmas.oregen3.editor.Editor;
import me.banbeucmas.oregen3.handlers.block.placetask.BlockPlaceTask;
import me.banbeucmas.oregen3.handlers.block.placetask.LimitedBlockPlaceTask;
import me.banbeucmas.oregen3.handlers.block.placetask.NormalBlockPlaceTask;
import me.banbeucmas.oregen3.handlers.event.*;
import me.banbeucmas.oregen3.hooks.placeholder.PlaceholderHandler;
import me.banbeucmas.oregen3.hooks.skyblock.*;
import me.banbeucmas.oregen3.listeners.*;
import me.banbeucmas.oregen3.utils.StringUtils;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Oregen3 extends JavaPlugin {
    private static Oregen3 plugin;
    private static SkyblockHook hook;
    private static Permission perm;
    private static PermissionManager permissionManager;
    private static BlockEventHandler eventHandler;
    private static BlockPlaceTask blockPlaceTask;
    public boolean papi;
    private boolean hasDependency = true;

    public boolean hasDependency() {
        return hasDependency;
    }

    public BlockEventHandler getEventHandler() {
        return eventHandler;
    }

    public void onDisable() {
        plugin = null;
        DataManager.unregisterAll();
    }

    public static SkyblockHook getHook() {
        return hook;
    }

    public static Oregen3 getPlugin() {
        return plugin;
    }

    public static Permission getPerm() {
        return perm;
    }

    public static PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public static BlockPlaceTask getBlockPlaceHandler() {
        return blockPlaceTask;
    }

    public void updateConfig() {
        reloadConfig();
        final FileConfiguration config = getConfig();
        eventHandler = config.getBoolean("global.listener.asyncListener", false) ?
                new AsyncBlockEventHandler(this) : new SyncBlockEventHandler();
        if (blockPlaceTask != null) {
            blockPlaceTask.stop();
        }
        blockPlaceTask = config.getLong("global.generators.maxBlockPlacePerTick", -1) > 0 ?
                new LimitedBlockPlaceTask(this) : new NormalBlockPlaceTask();
    }

    @Override
    public void onEnable() {
        plugin = this;

        new Metrics(this, 3052);

        saveDefaultConfig();
        updateConfig();
        checkDependency();
        setupPermissions();

        final CommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage("§7§m-------------§f[Oregen3§f]§7-------------");
        sender.sendMessage("");
        sender.sendMessage("   §fPlugin made by §eBanbeucmas§f, updated by §exHexed");
        sender.sendMessage("   §fVersion: §e" + getDescription().getVersion());
        sender.sendMessage("");
        sender.sendMessage("------------------------------------");

        DataManager.loadData();

        final PluginCommand command = Objects.requireNonNull(getCommand("oregen3"));
        command.setExecutor(new CommandHandler());
        command.setTabCompleter(new CommandHandler());

        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryEventsHandler(this), this);
        getServer().getPluginManager().registerEvents(new ChatEventHandler(this), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Editor.chanceSet.containsKey(player.getUniqueId())) {
                    player.sendTitle("§6§lEdit Chance", "§7Type in chat to edit, chat §ccancel§7 to cancel", 0, 5, 0);
                }
            }
        }, 0, 20);
    }

    public void reload() {
        updateConfig();
        DataManager.loadData();
    }

    private void checkDependency() {
        if (!getConfig().getBoolean("enableDependency")) {
            hasDependency = false;
            return;
        }

        final PluginManager manager = getServer().getPluginManager();
        if (manager.isPluginEnabled("ASkyBlock")) {
            hook     = new ASkyblockHook();
        }
        else if (manager.isPluginEnabled("AcidIsland")) {
            hook     = new AcidIslandHook();
        }
        else if (manager.isPluginEnabled("BentoBox")) {
            hook     = new BentoBoxHook();
        }
        else if (manager.isPluginEnabled("SuperiorSkyblock2")) {
            hook     = new SuperiorSkyblockHook();
        }
        else if (manager.isPluginEnabled("FabledSkyBlock")) {
            hook     = new FabledSkyBlockHook();
        }
        else if (manager.isPluginEnabled("uSkyBlock")) {
            hook     = new uSkyBlockHook();
        }
        else {
            getLogger().warning(StringUtils.getPrefixString("Plugin dependency for Oregen3 not found! Turning enableDependency off...", null));
            hasDependency = false;
        }

        if (manager.isPluginEnabled("PlaceholderAPI")) {
            papi = true;
            new PlaceholderHandler().register();
        }
    }

    private void setupPermissions() {
        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            perm              = Objects.requireNonNull(getServer().getServicesManager().getRegistration(Permission.class)).getProvider();
            permissionManager = new VaultPermission();
            if (getConfig().getBoolean("hooks.Vault.forceAsync")) {
                permissionManager = new AsyncVaultPermission(this);
            }
            else {
                Bukkit.getScheduler().runTask(this, () -> {
                    for (final String s : getConfig().getStringList("hooks.Vault.pluginAsyncList")) {
                        if (getServer().getPluginManager().isPluginEnabled(s)) {
                            permissionManager = new AsyncVaultPermission(this);
                            break;
                        }
                    }
                });
            }
        }
        else {
            getLogger().warning(StringUtils.getPrefixString("Vault not found! Offline player's permission will not be checked! Using bukkit's provided one...", null));
            permissionManager = new DefaultPermission();
        }
    }


}
