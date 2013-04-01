package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.idragonfire.DragonAntiPvPLeaver.DAPL_Config;
import com.github.idragonfire.DragonAntiPvPLeaver.Plugin;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DNpcManager;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnCheckerManager;

public class Listener_Normal implements Listener {
    protected DAPL_Config config;
    protected DNpcManager npcManager;
    protected DSpawnCheckerManager spawnModeChecker;
    protected DamageListenerHandler listenerInjectionHandler;

    public void init(DAPL_Config config, DNpcManager npcManager) {
        this.config = config;
        this.npcManager = npcManager;
    }

    public void setSpawnChecker(DSpawnCheckerManager spawnModeChecker) {
        this.spawnModeChecker = spawnModeChecker;
    }

    public void setListenerInjection(DamageListenerHandler listener) {
        listenerInjectionHandler = listener;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        int lifetime = spawnModeChecker.dragonNpcSpawnTime(player);
        if (lifetime == DSpawnCheckerManager.NO_SPAWN) {
            return;
        }
        npcManager.spawnHumanNPC(player, lifetime);
        if (config.plugin_printMessages) {
            String npcSpawned = config.language_npcSpawned;
            Plugin.broadcastNearPlayer(player, ChatColor.RED + player.getName()
                    + ChatColor.YELLOW + " " + npcSpawned,
                    config.npc_broadcastMessageRadius);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // TODO: punishment item
        // player.setItemInHand(DAntiPvPLeaverPlugin.setItemNameAndLore(
        // new ItemStack(Material.STICK), ChatColor.GOLD
        // + "DragonAntiPvpLeaver", new String[] {
        // "Your NPC was killed",
        // ChatColor.RED + "NEVER LOG OUT IN COMBAT" }));
        npcManager.spawnHumanNPC(event.getPlayer(), 5*60*60);
        System.out.println("spawn");
        if (spawnModeChecker.canBypass(player)) {
            return;
        }
        String name = player.getName();
        npcManager.despawnHumanByName(name);
        if (!npcManager.wasKilled(name)) {
            return;
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setHealth(0);
        if (config.plugin_printMessages) {
            player.sendMessage(ChatColor.RED + " "
                    + config.language_yourNpcKilled);
        }
        npcManager.removeKilledStatus(player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        // TODO: get npc id
        if (!npcManager.isMyNpc(event.getEntity())) {
            return;
        }
        String name = event.getEntity().getName();
        System.out.println("npc dead");
        // HumanNPC npc = (HumanNPC) this.antiPvP.getOneHumanNPCByName(event
        // .getEntity().getName());
        // TODO: use own NPC class
        // if (this.antiPvP.hasVanillaExpDrop()) {
        // event.setDroppedExp(npc.getDroppedExp());
        // }
        npcManager.addKillStatus(name);
        if (config.plugin_printMessages) {
            Plugin.broadcastNearPlayer(event.getEntity(), ChatColor.RED
                    + config.language_npcKilled.replace("<Player>", name),
                    config.npc_broadcastMessageRadius);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageEvent event) {
        if (listenerInjectionHandler != null) {
            listenerInjectionHandler.onEntityDamageByEntity(event);
        }
        try {
            if (!npcManager.isMyNpc(event.getEntity())) {
                return;
            }
            Player npc = (Player) event.getEntity();
            npcManager.npcAttackEvent(npc.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}