package com.killersmurf.antipvplogger.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.killersmurf.antipvplogger.AntiPvPLogger;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.topcat.npclib.entity.HumanNPC;

public class AntiPvPLoggerListener implements Listener {
    private AntiPvPLogger antiPvPLogger;

    public AntiPvPLoggerListener(AntiPvPLogger antiPvPLogger) {
        this.antiPvPLogger = antiPvPLogger;
    }

    public static boolean canBypass(Player player) {
        return player.hasPermission("antipvplogger.bypass")
                & !player.hasPermission("-antipvplogger.bypass");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (canBypass(player) || (player.getGameMode().getValue() == 1)) {
            return;
        }
        String name = player.getName();
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            WorldGuardPlugin worldGuard = (WorldGuardPlugin) Bukkit
                    .getPluginManager().getPlugin("WorldGuard");
            LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
            RegionManager regionManager = worldGuard.getRegionManager(player
                    .getWorld());
            ApplicableRegionSet set = regionManager.getApplicableRegions(player
                    .getLocation());
            if (!set.allows(DefaultFlag.PVP, localPlayer)) {
                return;
            }
        }
        if (!this.antiPvPLogger.playersNearby(player)) {
            return;
        }
        this.antiPvPLogger.spawnHumanNPC(player, player.getLocation(), name);
        AntiPvPLoggerListener.broadcastNearPlayer(player, ChatColor.RED
                + player.getName() + ChatColor.YELLOW + " NPC spawned.");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (canBypass(player)) {
            return;
        }
        final String name = player.getName();
        this.antiPvPLogger.despawnHumanByName(name);
        if (!this.antiPvPLogger.isDead(name)) {
            return;
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.sendMessage(ChatColor.RED
                + "Your NPC was killed while Combat Logged!");
        this.antiPvPLogger.removeDead(player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!this.antiPvPLogger.isAntiPvpNPC(event.getEntity())) {
            return;
        }
        HumanNPC npc = (HumanNPC) this.antiPvPLogger.getOneHumanNPCByName(event
                .getEntity().getName());
        this.antiPvPLogger.addDead(npc.getName());
        Bukkit.broadcastMessage(ChatColor.RED + npc.getName() + "'s"
                + ChatColor.YELLOW
                + " NPC has been killed while combat logged!");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageEvent event) {
        try {
            if (!this.antiPvPLogger.isAntiPvpNPC(event.getEntity())) {
                return;
            }
            Player npc = (Player) event.getEntity();
            this.antiPvPLogger.npcFirstTimeAttacked(npc.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void broadcastNearPlayer(Player playerForRadiusBroadcast,
            String message) {
        List<Player> players = playerForRadiusBroadcast.getWorld().getPlayers();
        Location loc = playerForRadiusBroadcast.getLocation();
        for (Player player : players) {
            if (player.getLocation().distance(loc) < 32) {
                player.sendMessage(message);
            }
        }
    }
}