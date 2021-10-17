package de.crafty.teleportable.events;

import de.crafty.teleportable.utils.ConfigManager;
import de.crafty.teleportable.utils.PlayerGlowHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerCrouchListener implements Listener {


    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Location playerPos = player.getLocation();
        Location blockPos = new Location(player.getWorld(), playerPos.getBlockX(), playerPos.getBlockY() - 1, playerPos.getBlockZ());

        if (!player.isSneaking()) {
            if (ConfigManager.hasLocationTeleportPad(blockPos)) {
                if (!PlayerGlowHandler.activeGlowing.contains(player)) {
                    PlayerGlowHandler.addGlowing(player);
                }
                PlayerGlowHandler.updateGlowing(player);
            }
        } else if (PlayerGlowHandler.activeGlowing.contains(player)) {
            PlayerGlowHandler.removeGlowing(player);
        }

    }


}
