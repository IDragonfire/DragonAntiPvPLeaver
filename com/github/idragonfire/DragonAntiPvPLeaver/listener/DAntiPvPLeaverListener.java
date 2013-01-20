package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.idragonfire.DragonAntiPvPLeaver.DAntiPvPLeaverPlugin;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DListenerInjection;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnCheckerManager;

public class DAntiPvPLeaverListener implements Listener {
    protected DAntiPvPLeaverPlugin antiPvP;
    protected DSpawnCheckerManager spawnModeChecker;
    protected ArrayList<DListenerInjection> listeners;

    public DAntiPvPLeaverListener(DAntiPvPLeaverPlugin antiPvP) {
        this.antiPvP = antiPvP;
        listeners = new ArrayList<DListenerInjection>();
    }

    public void addListener(DamageTimeListenerInjection listener) {
        listeners.add(listener);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        int lifetime = spawnModeChecker.dragonNpcSpawnTime(player);
        if (lifetime == DSpawnCheckerManager.NO_SPAWN) {
            return;
        }
        antiPvP.spawnHumanNPC(player, lifetime);
        if (antiPvP.printMessages()) {
            String npcSpawned = antiPvP.config.language_npcSpawned;
            antiPvP.broadcastNearPlayer(player, ChatColor.RED
                    + player.getName() + ChatColor.YELLOW + " " + npcSpawned);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        System.out.println("test");
        antiPvP.spawnHumanNPC(player, 5);
        // TODO: punishment item
        // player.setItemInHand(DAntiPvPLeaverPlugin.setItemNameAndLore(
        // new ItemStack(Material.STICK), ChatColor.GOLD
        // + "DragonAntiPvpLeaver", new String[] {
        // "Your NPC was killed",
        // ChatColor.RED + "NEVER LOG OUT IN COMBAT" }));
        if (spawnModeChecker.canBypass(player)) {
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
                    + antiPvP.config.language_yourNpcKilled);
        }
        antiPvP.removeDead(player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        // TODO: get npc id
        if (!antiPvP.isAntiPvpNPC(event.getEntity())) {
            return;
        }
        System.out.println("npc dead");
        // HumanNPC npc = (HumanNPC) this.antiPvP.getOneHumanNPCByName(event
        // .getEntity().getName());
        // TODO: use own NPC class
        // if (this.antiPvP.hasVanillaExpDrop()) {
        // event.setDroppedExp(npc.getDroppedExp());
        // }
        // this.antiPvP.addDead(npc.getName());
        // if (this.antiPvP.printMessages()) {
        // Bukkit.broadcastMessage(ChatColor.RED
        // + this.antiPvP.getLang("npcKilled").replace("<Player>",
        // npc.getName()));
        // }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageEvent event) {
        for (DListenerInjection listener : listeners) {
            listener.onEntityDamageByEntity(event);
        }
        try {
            if (!antiPvP.isAntiPvpNPC(event.getEntity())) {
                return;
            }
            Player npc = (Player) event.getEntity();
            // this.antiPvP.npcFirstTimeAttacked(npc.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}