package com.github.idragonfire.DragonAntiPvPLeaver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DFakePlayerManager;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DPlayerListener;

public class DAPL_Human_Manager implements DFakePlayerManager {
    protected DAPL_Plugin plugin;
    protected HashMap<String, Object> playerConnections;
    protected HashMap<String, DeSpawnTask> taskMap;
    protected List<DPlayerListener> listeners;

    public static final String FIELD_DELAYED = "dragonfire_dapl_delay_disconnect";
    public static final String FIELD_CONTINUE = "dragonfire_dapl_continue_disconnect";

    public DAPL_Human_Manager(DAPL_Plugin plugin) {
        this.plugin = plugin;
        playerConnections = new HashMap<String, Object>();
        taskMap = new HashMap<String, DeSpawnTask>();
        listeners = new ArrayList<DPlayerListener>();
    }

    @Override
    public boolean isMyNpc(Entity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }
        return playerConnections.containsKey(((Player) entity).getName());
    }

    @Override
    public void despawnHumanByName(String playerName) {
        Object playerConnection = playerConnections.get(playerName);
        if (playerConnection == null) {
            return;
        }
        despawnPlayer(playerName, playerConnection);
    }

    private void despawnPlayer(String playerName, Object playerConnection) {
        try {
            Field f = playerConnection.getClass().getDeclaredField(
                    FIELD_CONTINUE);
            f.set(playerConnection, true);
            playerConnections.remove(playerName);
            for (DPlayerListener listener : listeners) {
                listener.playerNpcDespawned(playerName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void npcAttackEvent(String name) {
        taskMap.get(name).increaseTime(
                plugin.config.npc_additionalTimeIfUnderAttack * 20L);
        for (DPlayerListener listener : listeners) {
            listener.playerNpcUnderAttack(name);
        }
    }

    @Override
    public void addKillStatus(String name) {
        plugin.deadPlayers.add(name);
        for (DPlayerListener listener : listeners) {
            listener.playerNpcKilled(name);
        }
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
    public void spawnHumanNPC(Player player, int lifetime,
            Object playerConnection) {
        String playerName = player.getName();
        playerConnections.put(playerName, playerConnection);
        DeSpawnTask task = new DeSpawnTask(playerName, this, plugin);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task,
                lifetime * 20L);
        taskMap.put(playerName, task);
        for (DPlayerListener listener : listeners) {
            listener.playerNpcSpawned(playerName);
        }
    }

    public void addDaplPlayerListener(DPlayerListener listener) {
        listeners.add(listener);
    }

    public void removeDaplPlayerListener(DPlayerListener listener) {
        listeners.remove(listener);
    }
}
