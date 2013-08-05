package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.github.idragonfire.DragonAntiPvPLeaver.DAntiPvPLeaverPlugin;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.mcore.ps.PS;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.topcat.npclib.DragonAntiPvPListener.entity.HumanNPC;

public class DAntiPvPLeaverListener implements Listener {
    protected DAntiPvPLeaverPlugin antiPvP;

    public DAntiPvPLeaverListener(DAntiPvPLeaverPlugin antiPvP) {
        this.antiPvP = antiPvP;
    }

    public static boolean canBypass(Player player) {
        return player.hasPermission("dragonantipvpleaver.bypass");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (canBypass(player) || (player.getGameMode().getValue() == 1)) {
            return;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
            Plugin factions = Bukkit.getPluginManager().getPlugin("Factions");

            String version = factions.getDescription().getVersion();
            if (version.startsWith("2")) {
                com.massivecraft.factions.entity.Faction faction = BoardColls
                        .get().getFactionAt(PS.valueOf(player.getLocation()));
                if (!faction.getFlag(com.massivecraft.factions.FFlag.PVP)
                        || faction
                                .getFlag(com.massivecraft.factions.FFlag.PEACEFUL)) {
                    return;
                }
                // TODO: remove old Factions support
            } else if (version.startsWith("1.6")) {
                if (Board.getFactionAt(new FLocation(player.getLocation()))
                        .isSafeZone()) {
                    return;
                }
            } else {
                // TODO: remove old Factions support
                Faction playerFaction = Board.getFactionAt(new FLocation(player
                        .getLocation()));
                if (!playerFaction.getFlag(FFlag.PVP)
                        || playerFaction.getFlag(FFlag.PEACEFUL)) {
                    return;
                }
            }
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
        if (!antiPvP.playersNearby(player)) {
            return;
        }
        antiPvP.spawnHumanNPC(player, player.getLocation(), name);
        if (antiPvP.printMessages()) {
            String npcSpawned = antiPvP.getLang("npcSpawned");
            antiPvP.broadcastNearPlayer(player, ChatColor.RED
                    + player.getName() + ChatColor.YELLOW + " " + npcSpawned);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // player.setItemInHand(DAntiPvPLeaverPlugin.setItemNameAndLore(
        // new ItemStack(Material.STICK), ChatColor.GOLD
        // + "DragonAntiPvpLeaver", new String[] {
        // "Your NPC was killed",
        // ChatColor.RED + "NEVER LOG OUT IN COMBAT" }));
        if (canBypass(player)) {
            return;
        }
        final String name = player.getName();
        antiPvP.despawnHumanByName(name);
        if (!antiPvP.isDead(name)) {
            return;
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setHealth(0);
        if (antiPvP.printMessages()) {
            player.sendMessage(ChatColor.RED + " "
                    + antiPvP.getLang("yourNPCKilled"));
        }
        antiPvP.removeDead(player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!antiPvP.isAntiPvpNPC(event.getEntity())) {
            return;
        }
        HumanNPC npc = antiPvP
                .getOneHumanNPCByName(event.getEntity().getName());
        // TODO: use own NPC class
        // if (this.antiPvP.hasVanillaExpDrop()) {
        // event.setDroppedExp(npc.getDroppedExp());
        // }
        antiPvP.addDead(npc.getName());
        if (antiPvP.printMessages()) {
            Bukkit.broadcastMessage(ChatColor.RED
                    + antiPvP.getLang("npcKilled").replace("<Player>",
                            npc.getName()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageEvent event) {
        try {
            if (!antiPvP.isAntiPvpNPC(event.getEntity())) {
                return;
            }
            Player npc = (Player) event.getEntity();
            antiPvP.npcFirstTimeAttacked(npc.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
