package de.crafty.teleportable.utils;

import de.crafty.teleportable.main.Teleportable;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    public static void saveTeleportPad(Location loc, String name) {
        HashMap<String, Location> pads = ConfigManager.getTeleportPads();
        ConfigManager.deleteTeleportPad(loc);
        pads.put(name, loc);
        ConfigManager.saveTeleportPads(pads);
    }

    public static void deleteTeleportPad(Location loc) {
        HashMap<String, Location> pads = ConfigManager.getTeleportPads();
        ArrayList<String> toRemove = new ArrayList<>();

        for (String s : pads.keySet()) {
            if (pads.get(s).equals(loc)) {
                toRemove.add(s);
            }
        }
        for (String s : toRemove) {
            pads.remove(s);
        }
        if (toRemove.size() > 0)
            ConfigManager.saveTeleportPads(pads);
    }

    public static void initConfig() {
        FileConfiguration config = Teleportable.getInstance().getConfig();

        if (!config.isSet("teleportPads")) {
            config.set("teleportPads", new ArrayList<>());
            Teleportable.getInstance().saveConfig();
        }
    }

    public static HashMap<String, Location> getTeleportPads() {
        HashMap<String, Location> pads = new HashMap<>();
        try {
            FileConfiguration config = Teleportable.getInstance().getConfig();
            Set<String> teleportPads = config.getConfigurationSection("teleportPads").getKeys(false);

            for (String s : teleportPads) {
                pads.put(s, config.getLocation("teleportPads." + s));
            }
        } catch (NullPointerException e) {

        }
        return pads;
    }

    public static String getTeleportPadByLoc(Location loc) {
        HashMap<String, Location> pads = ConfigManager.getTeleportPads();
        for (String s : pads.keySet()) {
            if (pads.get(s).equals(loc)) {
                return s;
            }
        }
        return null;
    }

    public static boolean hasLocationTeleportPad(Location loc) {
        return ConfigManager.getTeleportPads().containsValue(loc);
    }

    public static HashMap<String, Location> getTeleportPadsInRange(Location loc, int range) {
        HashMap<String, Location> pads = ConfigManager.getTeleportPads();
        HashMap<String, Location> padsInRange = new HashMap<>();
        for (String s : pads.keySet()) {
            Location l = pads.get(s);
            if ((l.getBlockX() >= loc.getBlockX() + range || l.getBlockX() <= loc.getBlockX() + range) && (l.getBlockZ() >= loc.getBlockZ() + range || l.getBlockZ() <= loc.getBlockZ() + range)) {
                padsInRange.put(s, l);
            }
        }

        return padsInRange;
    }

    private static void saveTeleportPads(HashMap<String, Location> map) {
        FileConfiguration config = Teleportable.getInstance().getConfig();

        config.set("teleportPads", new ArrayList<>());
        for (String s : map.keySet()) {
            config.set("teleportPads." + s, map.get(s));
        }
        Teleportable.getInstance().saveConfig();
    }


}
