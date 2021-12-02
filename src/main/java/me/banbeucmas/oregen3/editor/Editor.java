package me.banbeucmas.oregen3.editor;

import me.banbeucmas.oregen3.data.Generator;
import me.banbeucmas.oregen3.editor.type.EditType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Editor {

    public static HashMap<UUID, EditType> editSet = new HashMap<>();
    public static HashMap<UUID, Object> optionSet = new HashMap<>();

    public static void markChanceSet(Player player, Generator generator, String material, int index) {
        editSet.put(player.getUniqueId(), EditType.SET_CHANCE);
        optionSet.put(player.getUniqueId(), new Object() {
            HashMap<String, Object> parse() {
                HashMap<String, Object> options = new HashMap<>();
                options.put("Generator", generator.getId());
                options.put("Material", material);
                options.put("Index", index);
                return options;
            }
        }.parse());
    }

    public static void markPermSet(Player player, Generator generator) {
        editSet.put(player.getUniqueId(), EditType.SET_PERMISSION);
        optionSet.put(player.getUniqueId(), new Object() {
            HashMap<String, Object> parse() {
                HashMap<String, Object> options = new HashMap<>();
                options.put("Generator", generator.getId());
                return options;
            }
        }.parse());
    }

    public static void markPrioritySet(Player player, Generator generator) {
        editSet.put(player.getUniqueId(), EditType.SET_PRIORITY);
        optionSet.put(player.getUniqueId(), new Object() {
            HashMap<String, Object> parse() {
                HashMap<String, Object> options = new HashMap<>();
                options.put("Generator", generator.getId());
                return options;
            }
        }.parse());
    }

    public static void markLevelSet(Player player, Generator generator) {
        editSet.put(player.getUniqueId(), EditType.SET_LEVEL);
        optionSet.put(player.getUniqueId(), new Object() {
            HashMap<String, Object> parse() {
                HashMap<String, Object> options = new HashMap<>();
                options.put("Generator", generator.getId());
                return options;
            }
        }.parse());
    }

    public static void markVolumePitchSet(Player player, Generator generator) {
        editSet.put(player.getUniqueId(), EditType.SET_VOLUME_PITCH);
        optionSet.put(player.getUniqueId(), new Object() {
            HashMap<String, Object> parse() {
                HashMap<String, Object> options = new HashMap<>();
                options.put("Generator", generator.getId());
                return options;
            }
        }.parse());
    }

    public static void clearPlayerMarking(Player player) {
        editSet.remove(player.getUniqueId());
    }
}
