package de.crafty.teleportable.events;

import de.crafty.teleportable.utils.ConfigManager;
import de.crafty.teleportable.utils.PlayerGlowHandler;
import de.crafty.teleportable.utils.TeleportHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.EntityShulker;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Objects;

public class BlockBreakListener implements Listener {


    @EventHandler
    public void onTeleportPadDestroy(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();

        if (block.getType().equals(Material.LODESTONE)) {
            if (ConfigManager.getTeleportPads().containsValue(loc)) {

                ConfigManager.deleteTeleportPad(loc);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 10);
                for(Player p : Bukkit.getOnlinePlayers()){
                    PlayerGlowHandler.updateGlowing(p);
                }
            }
        }
    }

    @EventHandler
    public void onTeleportPadGetsDestroyed(EntityExplodeEvent event){

        for(Block block : event.blockList()){
            if(block.getType().equals(Material.LODESTONE)){
                if(ConfigManager.hasLocationTeleportPad(block.getLocation())){
                    ConfigManager.deleteTeleportPad(block.getLocation());
                    for(Player p : Bukkit.getOnlinePlayers()){
                        PlayerGlowHandler.updateGlowing(p);
                    }
                }
            }
        }
    }

}
