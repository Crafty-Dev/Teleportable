package de.crafty.teleportable.utils;

import de.crafty.teleportable.main.Teleportable;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftShulker;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerGlowHandler {

    public static final ArrayList<Player> activeGlowing = new ArrayList<>();
    public static final HashMap<Player, ArrayList<EntityShulker>> glowEntities = new HashMap<>();


    public static void addGlowing(Player player) {
        Location blockPos = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
        HashMap<String, Location> pads = ConfigManager.getTeleportPadsInRange(blockPos, Teleportable.TELEPORT_RANGE);

        CraftPlayer cPlayer = (CraftPlayer) player;
        World nmsWorld = ((CraftWorld) player.getWorld()).getHandle();

        PlayerGlowHandler.glowEntities.put(player, new ArrayList<>());
        PlayerGlowHandler.activeGlowing.add(player);

        for (String s : pads.keySet()) {
            Location loc = pads.get(s);

            double x = loc.getBlockX() + 0.5D;
            double y = loc.getBlockY();
            double z = loc.getBlockZ() + 0.5D;

            EntityShulker shulker = new EntityShulker(EntityTypes.SHULKER, nmsWorld);
            shulker.setInvisible(true);
            shulker.persistentInvisibility = true;
            shulker.setPosition(x, y, z);
            shulker.setNoAI(true);
            shulker.setInvulnerable(true);
            shulker.setPersistent();

            if(!loc.equals(blockPos))
                CraftEntity.getEntity((CraftServer) Bukkit.getServer(), shulker).setGlowing(true);

            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(shulker);
            cPlayer.getHandle().playerConnection.sendPacket(packet);

            PacketPlayOutEntityMetadata packet1 = new PacketPlayOutEntityMetadata(shulker.getId(), shulker.getDataWatcher(), true);
            cPlayer.getHandle().playerConnection.sendPacket(packet1);

            PlayerGlowHandler.glowEntities.get(player).add(shulker);
        }
    }

    public static void removeGlowing(Player player) {

        if (activeGlowing.contains(player)) {
            for (EntityShulker shulker : glowEntities.get(player)) {

                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(shulker.getId());
                CraftPlayer cPlayer = (CraftPlayer) player;
                cPlayer.getHandle().playerConnection.sendPacket(packet);
            }

            glowEntities.remove(player);
            activeGlowing.remove(player);
        }
    }

    public static void updateGlowing(Player player){
        Location usedLoc = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
        if(activeGlowing.contains(player)){
            for(EntityShulker shulker : glowEntities.get(player)){
                CraftEntity entity = CraftEntity.getEntity(((CraftServer) Bukkit.getServer()), shulker);

                int x = entity.getLocation().getBlockX();
                int y = entity.getLocation().getBlockY();
                int z = entity.getLocation().getBlockZ();

                if(!ConfigManager.hasLocationTeleportPad(new Location(player.getWorld(), x, y, z))){
                    PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getEntityId());
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
                    glowEntities.get(player).remove(shulker);
                    continue;
                }
                if(!entity.isGlowing() && !(x == usedLoc.getBlockX() && y == usedLoc.getBlockY() && z == usedLoc.getBlockZ())){
                    entity.setGlowing(true);
                    PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(shulker.getId(), shulker.getDataWatcher(), true);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                }

                if(x == usedLoc.getBlockX() && y == usedLoc.getBlockY() && z == usedLoc.getBlockZ()){
                    entity.setGlowing(false);
                    PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(shulker.getId(), shulker.getDataWatcher(), true);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                }
            }
        }
    }
}
