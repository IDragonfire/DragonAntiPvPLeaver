package com.github.idragonfire.DragonAntiPvPLeaver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.server.v1_4_R1.EntityHuman;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DNpcManager;

import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.api.DespawnReason;
import de.kumpelblase2.remoteentities.api.RemoteEntityType;
import de.kumpelblase2.remoteentities.api.thinking.DamageBehavior;
import de.kumpelblase2.remoteentities.api.thinking.Mind;
import de.kumpelblase2.remoteentities.api.thinking.goals.DesireFindNearestTarget;
import de.kumpelblase2.remoteentities.entities.RemotePlayer;
import de.kumpelblase2.remoteentities.entities.RemotePlayerEntity;

public class DAPL_NpcManager implements DNpcManager {

    private EntityManager npcManager;
    private HashMap<String, RemotePlayer> playerNPCs;
    private HashSet<Entity> bukkitEntities;
    private Plugin plugin;
    protected Map<String, DeSpawnTask> taskMap;

    public DAPL_NpcManager(EntityManager npcManager, Plugin plugin) {
        this.npcManager = npcManager;
        playerNPCs = new HashMap<String, RemotePlayer>();
        bukkitEntities = new HashSet<Entity>();
        this.plugin = plugin;
        taskMap = new HashMap<String, DeSpawnTask>();
    }

    @Override
    public boolean isMyNpc(Entity entity) {
        return bukkitEntities.contains(entity);
    }

    @Override
    public void despawnHumanByName(String npcID) {
        if (playerNPCs.containsKey(npcID)) {
            playerNPCs.get(npcID).despawn(DespawnReason.CUSTOM);
        }
    }

    @Override
    public void spawnHumanNPC(Player player, int lifetime) {
        // TODO: ChatColor for NPC name?
        RemotePlayer remoteEntity = (RemotePlayer) npcManager
                .createNamedEntity(RemoteEntityType.Human,
                        player.getLocation(), ChatColor.RED + player.getName());
        Mind mind = remoteEntity.getMind();
        final String npcID = "DragonPlayerNPC_" + player.getName();
        mind.addBehaviour(new DamageBehavior(remoteEntity) {
            @Override
            public void onRemove() {
                System.out.println("npc dead 2");
                super.onRemove();
            }

            @Override
            public void onDamage(EntityDamageEvent event) {
                System.out.println("event");
                npcAttackEvent(npcID);
            }
        });

        remoteEntity.getMind().addActionDesire(
                new DesireFindNearestTarget(remoteEntity, EntityHuman.class,
                        64f, false, 100), 1);

        RemotePlayerEntity remotePlayerEntity = (RemotePlayerEntity) remoteEntity
                .getHandle();
        // TODO: use kumpelblase function
        // remotePlayerEntity.setSameInventoryAs(player);

        playerNPCs.put(npcID, remoteEntity);
        bukkitEntities.add(remoteEntity.getBukkitEntity());

        DeSpawnTask task = new DeSpawnTask(npcID, this, plugin);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task,
                lifetime * 20L);
        taskMap.put(npcID, task);
    }

    public void npcAttackEvent(String name) {
        System.out.println("increase time");
        System.out.println(name);
        taskMap.get(name).increaseTime(
                plugin.config.npc_additionalTimeIfUnderAttack * 20L);
    }

    @Override
    public void addKillStatus(String name) {
        plugin.deadPlayers.add(name);
    }

    @Override
    public void removeKilledStatus(String name) {
        plugin.deadPlayers.remove(name);
    }

    @Override
    public boolean wasKilled(String name) {
        return plugin.deadPlayers.contains(name);
    }
}
