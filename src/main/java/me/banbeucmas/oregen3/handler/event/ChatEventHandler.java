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

        if (Editor.editSet.containsKey(player.getUniqueId())) {
            event.setCancelled(true);

            Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Oregen3.class), () -> {
                if (message.equalsIgnoreCase("cancel")) {
                    player.sendMessage("§8[§aOregen3§8]§7 Edit cancel!");
                    Editor.clearPlayerMarking(player);
                    return;
                }

                EditType type = Editor.editSet.get(player.getUniqueId());
                if (type == EditType.SET_CHANCE) {

                    double value;
                    try {
                        value = Double.parseDouble(message);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§8[§aOregen3§8]§7 §cInvalid input, set chance canceled");
                        Editor.clearPlayerMarking(player);
                        return;
                    }

                    HashMap<UUID, Object> options = (HashMap<UUID, Object>) Editor.optionSet.get(player.getUniqueId());
                    String generator = (String) options.get("Generator");
                    String material = (String) options.get("Material");
                    int index = (int) options.get("Index");
                    if (value < 0.0 || value > 100.0) {
                        player.sendMessage("§8[§aOregen3§8]§7 §cInvalid input, set chance canceled");
                        Editor.clearPlayerMarking(player);
                        return;
                    }

                    plugin.getConfig().set("generators." + generator + ".random." + material, value);
                    plugin.saveConfig();
                    Oregen3.getPlugin().reload();
                    player.sendMessage("§8[§aOregen3§8]§7 Set chance for material §2" + material + "§7 to §6" + message +"%");
                    Editor.clearPlayerMarking(player);
                }
                if (type == EditType.SET_PERMISSION) {
                    HashMap<UUID, Object> options = (HashMap<UUID, Object>) Editor.optionSet.get(player.getUniqueId());
                    String generator = (String) options.get("Generator");

                    plugin.getConfig().set("generators." + generator + ".permission", message);
                    plugin.saveConfig();
                    Oregen3.getPlugin().reload();
                    player.sendMessage("§8[§aOregen3§8]§7 Set permission for generator §6" + generator + "§7 to §6" + message);
                    Editor.clearPlayerMarking(player);
                }
                if (type == EditType.SET_PRIORITY) {
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

                    plugin.getConfig().set("generators." + generator + ".priority", value);
                    plugin.saveConfig();
                    Oregen3.getPlugin().reload();
                    player.sendMessage("§8[§aOregen3§8]§7 Set priority for generator §6" + generator + "§7 to §6" + value);
                    Editor.clearPlayerMarking(player);
                }
                if (type == EditType.SET_LEVEL) {
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

                    plugin.getConfig().set("generators." + generator + ".level", value);
                    plugin.saveConfig();
                    Oregen3.getPlugin().reload();
                    player.sendMessage("§8[§aOregen3§8]§7 Set level for generator §6" + generator + "§7 to §6" + value);
                    Editor.clearPlayerMarking(player);
                }
                if (type == EditType.SET_VOLUME_PITCH) {
                    HashMap<UUID, Object> options = (HashMap<UUID, Object>) Editor.optionSet.get(player.getUniqueId());
                    String generator = (String) options.get("Generator");

                    int volume;
                    int pitch;
                    String[] value = message.split("-|\\\\.");
                    try {
                        volume = Integer.parseInt(value[0]);
                        pitch = Integer.parseInt(value[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§8[§aOregen3§8]§7 §cInvalid input, set volume-pitch canceled");
                        Editor.clearPlayerMarking(player);
                        return;
                    }

                    plugin.getConfig().set("generators." + generator + ".sound.volume", volume);
                    plugin.getConfig().set("generators." + generator + ".sound.pitch", pitch);
                    plugin.saveConfig();
                    Oregen3.getPlugin().reload();
                    player.sendMessage(new String[] {
                            "",
                            "§8[§aOregen3§8]§7 Set level for generator §6" + generator + "§7 to",
                            "§7Volume: §6" + volume,
                            "§7Pitch: §6" + pitch,
                            ""
                    });
                    Editor.clearPlayerMarking(player);
                }
            });
        }
    }
}
