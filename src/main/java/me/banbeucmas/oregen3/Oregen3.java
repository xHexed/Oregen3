package me.banbeucmas.oregen3;

import lombok.AccessLevel;
import lombok.Getter;
import me.banbeucmas.oregen3.commands.CommandHandler;
import me.banbeucmas.oregen3.data.DataManager;
import me.banbeucmas.oregen3.data.permission.*;
import me.banbeucmas.oregen3.handler.block.placetask.BlockPlaceTask;
import me.banbeucmas.oregen3.handler.block.placetask.LimitedBlockPlaceTask;
import me.banbeucmas.oregen3.handler.block.placetask.NormalBlockPlaceTask;
import me.banbeucmas.oregen3.handler.event.AsyncBlockEventHandler;
import me.banbeucmas.oregen3.handler.event.BlockEventHandler;
import me.banbeucmas.oregen3.handler.event.SyncBlockEventHandler;
import me.banbeucmas.oregen3.hook.placeholder.PlaceholderHandler;
import me.banbeucmas.oregen3.hook.skyblock.*;
import me.banbeucmas.oregen3.listener.BlockBreakListener;
import me.banbeucmas.oregen3.listener.BlockListener;
import me.banbeucmas.oregen3.listener.ChatListener;
import me.banbeucmas.oregen3.listener.InventoryListener;
import me.banbeucmas.oregen3.manager.ConfigManager;
import me.banbeucmas.oregen3.util.BlockChecker;
import me.banbeucmas.oregen3.util.PluginUtils;
import me.banbeucmas.oregen3.util.StringParser;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
@Getter
public class Oregen3 extends JavaPlugin {
    private boolean papiEnabled;
    @Getter(AccessLevel.NONE) private boolean hasDependency = true;
    public boolean hasDependency() {
        return hasDependency;
    }

    private SkyblockHook hook;
    private Permission perm;
    private PermissionChecker permissionChecker;
    private BlockEventHandler blockEventHandler;
    private BlockPlaceTask blockPlaceTask;
    private PluginUtils utils = new PluginUtils(this);
    private StringParser stringParser = new StringParser(this);
    private BlockChecker blockChecker = new BlockChecker(this);
    private DataManager dataManager = new DataManager(this);
    private ChatListener chatListener = new ChatListener(this);
    private ConfigManager configManager = new ConfigManager(this);

    @Getter(AccessLevel.NONE)
    private BlockBreakListener blockBreakListener;

    private File configFolder = new File(getDataFolder(), "config.yml");

    public void onDisable() {
        dataManager.unregisterAll();
    }

    public void updateConfig() {
        reloadConfig();
        blockEventHandler = getConfig().getBoolean("global.listener.asyncListener", false) ?
                new AsyncBlockEventHandler(this) : new SyncBlockEventHandler(this);
        if (blockPlaceTask != null) {
            blockPlaceTask.stop();
        }
        blockPlaceTask = getConfig().getLong("global.generators.maxBlockPlacePerTick", -1) > 0 ?
                new LimitedBlockPlaceTask(this) : new NormalBlockPlaceTask(this);

        if (getConfig().getBoolean("global.generators.check-regen.enabled", false)) {
            if (blockBreakListener == null) {
                blockBreakListener = new BlockBreakListener(this);
                getServer().getPluginManager().registerEvents(blockBreakListener, this);
            }
        }
        else if (blockBreakListener != null) {
            BlockBreakEvent.getHandlerList().unregister(blockBreakListener);
            blockBreakListener = null;
        }
    }

    @Override
    public void onEnable() {
        new MetricsLite(this, 3052);

        saveDefaultConfig();
        updateConfig();
        checkDependency();
        setupPermissions();

        final CommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage("§7§m-------------§f[Oregen3§f]§7-------------");
        sender.sendMessage("");
        sender.sendMessage("   §fPlugin made by §eBanbeucmas§f, updated by §exHexed§f, contributed by §eGalaxyVN");
        sender.sendMessage("   §fVersion: §e" + getDescription().getVersion());
        sender.sendMessage("");
        sender.sendMessage("------------------------------------");

        dataManager.loadData();

        final PluginCommand command = Objects.requireNonNull(getCommand("oregen3"));
        CommandHandler commandHandler = new CommandHandler(this);
        command.setExecutor(commandHandler);
        command.setTabCompleter(commandHandler);
        
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(chatListener, this);
    }

    public void reload() {
        updateConfig();
        dataManager.loadData();
    }

    private void checkDependency() {
        if (!getConfig().getBoolean("enableDependency")) {
            hasDependency = false;
            return;
        }

        final PluginManager manager = getServer().getPluginManager();
        if (manager.isPluginEnabled("ASkyBlock")) {
            hook = new ASkyblockHook();
        }
        else if (manager.isPluginEnabled("AcidIsland")) {
            hook = new AcidIslandHook();
        }
        else if (manager.isPluginEnabled("BentoBox")) {
            hook = new BentoBoxHook(this);
        }
        else if (manager.isPluginEnabled("SuperiorSkyblock2")) {
            hook = new SuperiorSkyblockHook();
        }
        else if (manager.isPluginEnabled("FabledSkyBlock")) {
            hook = new FabledSkyBlockHook();
        }
        else if (manager.isPluginEnabled("uSkyBlock")) {
            hook = new uSkyBlockHook();
        }
        else if (manager.isPluginEnabled("IridiumSkyblock")) {
            hook = new IridiumSkyblockHook();
        }
        else {
            getLogger().warning("Plugin dependency for Oregen3 not found! Turning enableDependency off...");
            hasDependency = false;
        }

        if (manager.isPluginEnabled("PlaceholderAPI")) {
            papiEnabled = true;
            new PlaceholderHandler(this).register();
        }
    }

    private void setupPermissions() {
        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            perm              = Objects.requireNonNull(getServer().getServicesManager().getRegistration(Permission.class)).getProvider();
            permissionChecker = new SyncVaultPermissionChecker(this);
            if (getConfig().getBoolean("hooks.Vault.forceAsync")) {
                if (getConfig().getBoolean("global.listener.asyncListener", false)) {
                    permissionChecker = new AsyncVaultPermissionChecker(this);
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
                                permissionChecker = new AsyncVaultPermissionChecker(this);
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
            getLogger().warning(stringParser.getColoredString("Vault not found! Offline player's permission will not be checked! Using bukkit's provided one...", null));
            permissionChecker = new DefaultPermissionChecker();
        }
    }


}
