package me.banbeucmas.oregen3;

import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import me.banbeucmas.oregen3.commands.CommandHandler;
import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.data.permission.*;
import me.banbeucmas.oregen3.handler.block.placetask.BlockPlaceTask;
import me.banbeucmas.oregen3.handler.block.placetask.LimitedBlockPlaceTask;
import me.banbeucmas.oregen3.handler.block.placetask.NormalBlockPlaceTask;
import me.banbeucmas.oregen3.handler.event.*;
import me.banbeucmas.oregen3.hook.placeholder.PlaceholderHandler;
import me.banbeucmas.oregen3.hook.skyblock.*;
import me.banbeucmas.oregen3.listener.BlockListener;
import me.banbeucmas.oregen3.listener.InventoryListener;
import me.banbeucmas.oregen3.util.StringUtils;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Oregen3 extends JavaPlugin {
    private static Oregen3 plugin;
    private static SkyblockHook hook;
    private static Permission perm;
    private static PermissionChecker permissionChecker;
    private static BlockEventHandler eventHandler;
    private static BlockPlaceTask blockPlaceTask;
    public boolean papi;
    private boolean hasDependency = true;
    private InventoryManager inventoryManager;

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

    public static PermissionChecker getPermissionManager() {
        return permissionChecker;
    }

    public static BlockPlaceTask getBlockPlaceHandler() {
        return blockPlaceTask;
    }

    public void updateConfig() {
        reloadConfig();
        eventHandler = getConfig().getBoolean("global.listener.asyncListener", false) ?
                new AsyncBlockEventHandler(this) : new SyncBlockEventHandler();
        if (blockPlaceTask != null) {
            blockPlaceTask.stop();
        }
        blockPlaceTask = getConfig().getLong("global.generators.maxBlockPlacePerTick", -1) > 0 ?
                new LimitedBlockPlaceTask(this) : new NormalBlockPlaceTask(this);
    }

    @Override
    public void onEnable() {
        plugin = this;

        new MetricsLite(this, 3052);

        saveDefaultConfig();
        updateConfig();
        checkDependency();
        setupPermissions();

        final CommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage("§7§m-------------§f[Oregen3§f]§7-------------");
        sender.sendMessage("");
        sender.sendMessage("   §fPlugin made by §eBanbeucmas§f, updated by §exHexed§f, contribute by §eGalaxyVN");
        sender.sendMessage("   §fVersion: §e" + getDescription().getVersion());
        sender.sendMessage("");
        sender.sendMessage("------------------------------------");

        DataManager.loadData();

        final PluginCommand command = Objects.requireNonNull(getCommand("oregen3"));
        command.setExecutor(new CommandHandler());
        command.setTabCompleter(new CommandHandler());

        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new ChatEventHandler(this), this);
        getServer().getPluginManager().registerEvents(new InventoryOpenHandler(), this);

        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.invoke();
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
            getLogger().warning("Plugin dependency for Oregen3 not found! Turning enableDependency off...");
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
            permissionChecker = new SyncVaultPermissionChecker();
            if (getConfig().getBoolean("hooks.Vault.forceAsync")) {
                if (getConfig().getBoolean("global.listener.asyncListener", false)) {
                    permissionChecker = new AsyncVaultPermissionChecker();
                }
                else {
                    permissionChecker = new AsyncOnSyncVaultPermissionChecker(this);
                }
            }
            else {
                Bukkit.getScheduler().runTask(this, () -> {
                    for (final String s : getConfig().getStringList("hooks.Vault.pluginAsyncList")) {
                        if (getServer().getPluginManager().isPluginEnabled(s)) {
                            if (getConfig().getBoolean("global.listener.asyncListener", false)) {
                                permissionChecker = new AsyncVaultPermissionChecker();
                            }
                            else {
                                permissionChecker = new AsyncOnSyncVaultPermissionChecker(this);
                            }
                            break;
                        }
                    }
                });
            }
        }
        else {
            getLogger().warning(StringUtils.getColoredString("Vault not found! Offline player's permission will not be checked! Using bukkit's provided one...", null));
            permissionChecker = new DefaultPermissionChecker();
        }
    }


}
