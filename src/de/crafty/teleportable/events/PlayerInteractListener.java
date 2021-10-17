package de.crafty.teleportable.events;

import de.crafty.teleportable.utils.ConfigManager;
import de.crafty.teleportable.utils.PlayerGlowHandler;
import de.crafty.teleportable.utils.TeleportHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.libs.org.apache.maven.artifact.repository.metadata.Metadata;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftShulker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class PlayerInteractListener implements Listener {


    @EventHandler
    public void onTeleportPadCreate(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        try {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                ItemStack heldItem = player.getItemInHand();
                Block block = event.getClickedBlock();
                World world = player.getWorld();

                if (block.getType().equals(Material.LODESTONE) && heldItem.getType().equals(Material.ENDER_EYE)) {
                    String name = "§unnamed";
                    if (heldItem.getItemMeta().hasDisplayName()) {
                        name = heldItem.getItemMeta().getDisplayName();
                        if (ConfigManager.getTeleportPads().containsKey(name)) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7The name §c" + name + " §7is already used for a teleport pad"));
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 10);
                            event.setCancelled(true);
                            return;
                        }
                    }
                    if (name.equals("§unnamed"))
                        name = "§tp-unnamed" + UUID.randomUUID();

                    if (ConfigManager.getTeleportPads().containsValue(block.getLocation())) {
                        if (!name.equals(ConfigManager.getTeleportPadByLoc(block.getLocation())) && !(name.startsWith("§") && Objects.requireNonNull(ConfigManager.getTeleportPadByLoc(block.getLocation())).startsWith("§"))) {

                            ConfigManager.deleteTeleportPad(block.getLocation());
                            ConfigManager.saveTeleportPad(block.getLocation(), name);
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 10, 10);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aRenamed §7teleport pad to §b" + (name.startsWith("§") ? "unnamed" : name)));

                            if(player.getGameMode() != GameMode.CREATIVE)
                                player.getItemInHand().setAmount(heldItem.getAmount() - 1);

                            event.setCancelled(true);
                            return;
                        }
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cThere is already a teleport pad"));
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 10);
                        event.setCancelled(true);
                        return;
                    }

                    ConfigManager.saveTeleportPad(block.getLocation(), name);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 10, 10);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aCreated §7teleport pad §b" + (name.startsWith("§") ? "unnamed" : name)));

                    if(player.getGameMode() != GameMode.CREATIVE)
                        player.getItemInHand().setAmount(heldItem.getAmount() - 1);

                    event.setCancelled(true);
                }
            }
        } catch (NullPointerException e) {
        }
    }


    @EventHandler
    public void onPlayerHit(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location playerLoc = player.getLocation();
        Location blockLoc = new Location(player.getWorld(), playerLoc.getBlockX(), playerLoc.getBlockY() - 1, playerLoc.getBlockZ());

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (ConfigManager.hasLocationTeleportPad(blockLoc) && player.isSneaking()) {
                Location tpLoc = TeleportHandler.getTeleportLocation(player);
                if (tpLoc != null && !tpLoc.equals(blockLoc)) {
                    String name = ConfigManager.getTeleportPadByLoc(tpLoc);
                    PlayerGlowHandler.removeGlowing(player);
                    player.teleport(new Location(player.getWorld(), tpLoc.getBlockX() + 0.5D, tpLoc.getBlockY() + 1.1D, tpLoc.getBlockZ() + 0.5D, playerLoc.getYaw(), playerLoc.getPitch()));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 10);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aTeleported §7to §b" + (name.startsWith("§") ? "unnamed" : name)));
                    event.setCancelled(true);
                }
            }
        }
    }
}
