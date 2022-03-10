package me.banbeucmas.oregen3.data;

import me.banbeucmas.oregen3.Oregen3;

import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private static final Map<String, Generator> choosers = new HashMap<>();

    public static Map<String, Generator> getChoosers() {
        return choosers;
    }

    public static void unregisterAll(){
        choosers.clear();
    }

    public static void loadData() {
        /*
        unregisterAll();
        Oregen3.getPlugin().getConfig()
                .getConfigurationSection("generators")
                .getKeys(false)
                .forEach(id -> choosers.put(id, new Generator(id)));*/
    }
}
