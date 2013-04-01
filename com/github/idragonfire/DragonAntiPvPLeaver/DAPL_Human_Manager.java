package com.github.idragonfire.DragonAntiPvPLeaver;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DNpcManager;

public class DAPL_Human_Manager implements DNpcManager {
    protected DAPL_Plugin plugin;
    protected HashMap<String, Object> playerConnections;
    protected HashMap<String, DeSpawnTask> taskMap;

    public DAPL_Human_Manager(DAPL_Plugin plugin) {
        this.plugin = plugin;
        this.playerConnections = new HashMap<String, Object>();
        this.taskMap = new HashMap<String, DeSpawnTask>();
    }

    @Override
    public boolean isMyNpc(Entity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }
        return playerConnections.containsKey(((Player) entity).getName());
    }

    @Override
    public void despawnHumanByName(String npcID) {
        Object playerConnection = playerConnections.get(npcID);
        if (playerConnection == null) {
            return;
        }
        despawnPlayer(playerConnection);
    }

    private void despawnPlayer(Object playerConnection) {
        try {
            Field f = playerConnection.getClass().getDeclaredField(
                    DAPL_Transformer.FIELD_CONTINUE);
            f.set(playerConnection, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void npcAttackEvent(String name) {
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

    @Override
    public void spawnHumanNPC(Player player, int lifetime) {
        String playerName = player.getName();
        DeSpawnTask task = new DeSpawnTask(playerName, this, plugin);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task,
                lifetime * 20L);
        taskMap.put(playerName, task);
    }

}
