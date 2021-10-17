package de.crafty.teleportable.utils;

import de.crafty.teleportable.main.Teleportable;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerUpdateHandler {


    public static void updatePlayers(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Teleportable.getInstance(), () -> {

            for(Player player : PlayerGlowHandler.activeGlowing){
                Location loc = TeleportHandler.getTeleportLocation(player);
                if(loc != null) {
                    String name = ConfigManager.getTeleportPadByLoc(loc);
                    PlayerUpdateHandler.setActionBar(player, "§7Teleport Pad: §b" + (name.startsWith("§") ? "unnamed" : name));
                }else
                    PlayerUpdateHandler.setActionBar(player, "§7Teleport Pad: §onone");
            }

        }, 0, 20);
    }


    public static void setActionBar(Player player, String text){
        IChatBaseComponent iChatBaseComponent = new ChatComponentText(text);
        PacketPlayOutChat packet = new PacketPlayOutChat(iChatBaseComponent, ChatMessageType.GAME_INFO, player.getUniqueId());
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
}
