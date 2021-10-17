package de.crafty.teleportable.main;

import de.crafty.teleportable.events.*;
import de.crafty.teleportable.utils.ConfigManager;
import de.crafty.teleportable.utils.PlayerUpdateHandler;
import de.crafty.teleportable.utils.TeleportHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Teleportable extends JavaPlugin {

    private static Teleportable instance;
    public static final int TELEPORT_RANGE = 48;

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager.initConfig();

        PlayerUpdateHandler.updatePlayers();

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new PlayerInteractListener(), this);
        pluginManager.registerEvents(new BlockBreakListener(), this);
        pluginManager.registerEvents(new PlayerCrouchListener(), this);
        pluginManager.registerEvents(new PlayerMoveListener(), this);

        Bukkit.getConsoleSender().sendMessage(References.PREFIX + "Plugin enabled");

    }

    @Override
    public void onDisable() {

        Bukkit.getConsoleSender().sendMessage(References.PREFIX + "Plugin disabled");

    }

    public static Teleportable getInstance() {
        return instance;
    }


}
