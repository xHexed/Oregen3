package me.banbeucmas.oregen3.handler.event;

import me.banbeucmas.oregen3.Oregen3;
import me.banbeucmas.oregen3.editor.Editor;
import me.banbeucmas.oregen3.editor.type.EditType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class ChatEventHandler implements Listener {

    private Oregen3 plugin;
    public ChatEventHandler(Oregen3 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        processAsyncChat(event, player, message);
    }

    private void processAsyncChat(AsyncPlayerChatEvent event, Player player, String message) {

        if (Editor.chanceSet.containsKey(player.getUniqueId())) {
            event.setCancelled(true);

            Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Oregen3.class), () -> {
                if (message.equalsIgnoreCase("cancel")) {
                    player.sendMessage("§8[§aOregen3§8]§7 Edit cancel!");
                    Editor.clearPlayerMarking(player);
                    return;
                }

                double value;
                try {
                    value = Double.parseDouble(message);
                } catch (NumberFormatException e) {
                    player.sendMessage("§8[§aOregen3§8]§7 §cInvalid input, set chance canceled");
                    Editor.clearPlayerMarking(player);
                    return;
                }

                EditType type = Editor.chanceSet.get(player.getUniqueId());
                if (type.equals(EditType.SET_CHANCE)) {
                    HashMap<UUID, Object> options = (HashMap<UUID, Object>) Editor.optionSet.get(player.getUniqueId());
                    String generator = (String) options.get("Generator");
                    String material = (String) options.get("Material");
                    if (value < 0.0 || value > 100.0) {
                        player.sendMessage("§8[§aOregen3§8]§7 §cInvalid input, set chance canceled");
                        Editor.clearPlayerMarking(player);
                        return;
                    }

                    // TODO: Save config with comments
                    plugin.getConfig().set("generators." + generator + ".random." + material, value);
                    plugin.saveConfig();
                    plugin.reload();
                    player.sendMessage("§8[§aOregen3§8]§7 Set chance for material §2" + material + "§7 to §6" + message + "%");
                    Editor.clearPlayerMarking(player);
                }
            });
        } else if (Editor.editSet.containsKey(player.getUniqueId())) {
            event.setCancelled(true);

            Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Oregen3.class), () -> {
                if (message.equalsIgnoreCase("cancel")) {
                    player.sendMessage("§8[§aOregen3§8]§7 Edit cancel!");
                    Editor.clearPlayerMarking(player);
                    return;
                }

                EditType type = Editor.editSet.get(player.getUniqueId());
                if (type.equals(EditType.SET_PERMISSION)) {
                    HashMap<UUID, Object> options = (HashMap<UUID, Object>) Editor.optionSet.get(player.getUniqueId());
                    String generator = (String) options.get("Generator");

                    // TODO: Save config with comments
                    plugin.getConfig().set("generators." + generator + ".permission", message);
                    plugin.saveConfig();
                    plugin.reload();
                    player.sendMessage("§8[§aOregen3§8]§7 Set permission for generator §2" + generator + "§7 to §6" + message);
                    Editor.clearPlayerMarking(player);
                } else if (type.equals(EditType.SET_PRIORITY)) {
                    HashMap<UUID, Object> options = (HashMap<UUID, Object>) Editor.optionSet.get(player.getUniqueId());
                    String generator = (String) options.get("Generator");

                    int value;
                    try {
                        value = Integer.parseInt(message);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§8[§aOregen3§8]§7 §cInvalid input, set priority canceled");
                        Editor.clearPlayerMarking(player);
                        return;
                    }

                    // TODO: Save config with comments
                    plugin.getConfig().set("generators." + generator + ".priority", value);
                    plugin.saveConfig();
                    plugin.reload();
                    player.sendMessage("§8[§aOregen3§8]§7 Set priotiry for generator §2" + generator + "§7 to §6" + value);
                    Editor.clearPlayerMarking(player);
                } else if (type.equals(EditType.SET_LEVEL)) {
                    HashMap<UUID, Object> options = (HashMap<UUID, Object>) Editor.optionSet.get(player.getUniqueId());
                    String generator = (String) options.get("Generator");

                    int value;
                    try {
                        value = Integer.parseInt(message);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§8[§aOregen3§8]§7 §cInvalid input, set level canceled");
                        Editor.clearPlayerMarking(player);
                        return;
                    }

                    // TODO: Save config with comments
                    plugin.getConfig().set("generators." + generator + ".level", value);
                    plugin.saveConfig();
                    plugin.reload();
                    player.sendMessage("§8[§aOregen3§8]§7 Set level for generator §2" + generator + "§7 to §6" + value);
                }
            });
        }
    }
}
