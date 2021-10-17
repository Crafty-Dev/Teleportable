package de.crafty.teleportable.utils;

import de.crafty.teleportable.main.Teleportable;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TeleportHandler {


    public static Location getTeleportLocation(Player player) {

        Location pLoc = player.getLocation();
        Location playerLoc = player.getEyeLocation();
        Location usedLoc = new Location(player.getWorld(), pLoc.getBlockX(), pLoc.getBlockY() - 1, pLoc.getBlockZ());

        HashMap<String, Location> pads = ConfigManager.getTeleportPadsInRange(playerLoc, Teleportable.TELEPORT_RANGE);
        HashMap<Location, Double> angles = new HashMap<>();

        pads.remove(ConfigManager.getTeleportPadByLoc(usedLoc));

        Block block = player.getTargetBlock(null, 5);

        if(block.getType().equals(Material.LODESTONE)){
            if(ConfigManager.hasLocationTeleportPad(block.getLocation()) && !block.getLocation().equals(usedLoc)){
                return block.getLocation();
            }
        }

        for (String s : pads.keySet()) {
            Location blockLoc = pads.get(s);

            double x = blockLoc.getBlockX() + 0.5D - playerLoc.getX();
            double y = blockLoc.getBlockY() + 0.5D - playerLoc.getY();
            double z = blockLoc.getBlockZ() + 0.5D - playerLoc.getZ();

            Vector blockVec = new Vector(x, y, z).normalize();
            Vector playerVec = playerLoc.getDirection().normalize();

            x = blockVec.getX();
            y = blockVec.getY();
            z = blockVec.getZ();

            double x1 = playerVec.getX();
            double y1 = playerVec.getY();
            double z1 = playerVec.getZ();

            double d = playerVec.dot(blockVec);
            double d1 = Math.sqrt(x * x + y * y + z * z);
            double d2 = Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);

            double degree = Math.acos(d / (d1 * d2));
            if (degree <= Math.toRadians(5)) {
                angles.put(blockLoc, degree);
            }
        }

        if (angles.size() == 0)
            return null;

        double d = Collections.min(angles.values());

        for (Location loc : angles.keySet()) {
            if (angles.get(loc).equals(d)) {
                return loc;
            }
        }
        return null;
    }




}
