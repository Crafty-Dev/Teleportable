package de.crafty.teleportable.events;

import de.crafty.teleportable.utils.ConfigManager;
import de.crafty.teleportable.utils.PlayerGlowHandler;
import de.crafty.teleportable.utils.PlayerUpdateHandler;
import de.crafty.teleportable.utils.TeleportHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {


    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Location playerPos = player.getLocation();
        Location blockPos = new Location(player.getWorld(), playerPos.getBlockX(), playerPos.getBlockY() - 1, playerPos.getBlockZ());

        Location from = event.getFrom();
        Location to = event.getTo();

        if (!ConfigManager.hasLocationTeleportPad(blockPos)) {
            if (PlayerGlowHandler.activeGlowing.contains(player)) {
                PlayerGlowHandler.removeGlowing(player);
            }
        } else if (!PlayerGlowHandler.activeGlowing.contains(player) && player.isSneaking()) {
            PlayerGlowHandler.addGlowing(player);
        }

        if (ConfigManager.hasLocationTeleportPad(blockPos)) {
            PlayerGlowHandler.updateGlowing(player);
        }

    }


    @EventHandler
    public void onDirectionChange(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location playerPos = player.getLocation();
        Location blockPos = new Location(player.getWorld(), playerPos.getBlockX(), playerPos.getBlockY() - 1, playerPos.getBlockZ());

        if (PlayerGlowHandler.activeGlowing.contains(player)) {
            Location loc = TeleportHandler.getTeleportLocation(player);
            if (loc != null) {
                String name = ConfigManager.getTeleportPadByLoc(loc);
                PlayerUpdateHandler.setActionBar(player, "§7Teleport Pad: §b" + (name.startsWith("§") ? "unnamed" : name));
            } else
                PlayerUpdateHandler.setActionBar(player, "§7Teleport Pad: §onone");

        }
    }

}
