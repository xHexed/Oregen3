package me.banbeucmas.oregen3.hook.placeholder;

import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.util.PluginUtils;
import me.banbeucmas.oregen3.util.StringUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderHandler extends PlaceholderExpansion {
    private static final HashMap<String, IdentifierHandler> identifierHandlers = new HashMap<>();

    static {
        identifierHandlers.put("generator", (player, params) -> {
            World world = null;
            if (params.length > 0) world = Bukkit.getWorld(params[0]);
            final Generator chooser = PluginUtils.getChosenGenerator(player.getUniqueId(), world);
            return chooser != null ? chooser.getName() : "";
        });
        identifierHandlers.put("random", (player, params) -> {
            if (params.length < 2) return "0";
            World world = null;
            if (params.length > 2) {
                world = Bukkit.getWorld(params[2]);
            }
            final Generator chooser = PluginUtils.getChosenGenerator(player.getUniqueId(), world);
            if (chooser == null)
                return "0";
            final Map<String, Double> chances = chooser.getRandom();
            return chances.containsKey(params[1]) ? StringUtils.DOUBLE_FORMAT.format(chances.get(params[1])) : "0";
        });
    }

    @Override
    public @NotNull String getIdentifier() {
        return "oregen3";
    }

    @Override
    public @NotNull String getAuthor() {
        return "xHexed";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() { return true; }

    @Override
    public boolean canRegister() { return true; }

    @Override
    public String onRequest(final OfflinePlayer player, @NotNull final String identifier) {
        if (player == null || identifier.isEmpty()) return "";
        final String[] params = identifier.split("_");
        if (identifierHandlers.containsKey(params[0].toLowerCase())) {
            return identifierHandlers.get(params[0]).handle(player, params);
        }
        else {
            return "";
        }
    }
}
