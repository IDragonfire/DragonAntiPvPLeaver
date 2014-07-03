package com.github.idragonfire.DragonAntiPvPLeaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DFakePlayerManager;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DPlayerListener;
import com.github.idragonfire.DragonAntiPvPLeaver.npclib.HumanNPC;
import com.github.idragonfire.DragonAntiPvPLeaver.npclib.NPCManager;

public class DAPL_Human_Manager extends NPCManager implements
		DFakePlayerManager {
	protected DAPL_Plugin plugin;
	protected HashMap<String, Object> playerConnections;
	protected HashMap<String, DeSpawnTask> taskMap;
	protected List<DPlayerListener> listeners;

	public static final String FIELD_DELAYED = "dragonfire_dapl_delay_disconnect";
	public static final String FIELD_CONTINUE = "dragonfire_dapl_continue_disconnect";

	public DAPL_Human_Manager(DAPL_Plugin plugin) {
		super(plugin);
		this.plugin = plugin;
		taskMap = new HashMap<String, DeSpawnTask>();
		listeners = new ArrayList<DPlayerListener>();
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
	public void spawnHumanNPC(Player player, int lifetime) {
		String playerName = player.getName();
		HumanNPC npc = this.spawnHumanNPC(playerName, player.getLocation());

		ItemStack[] invContents = player.getInventory().getContents();
		ItemStack[] armourContents = player.getInventory().getArmorContents();
		npc.getInventory().setContents(invContents);
		npc.getInventory().setArmorContents(armourContents);

		// Formula for calculating dropped XP
		int XP = player.getLevel() * 7;
		if (XP > 100) {
			XP = 100;
		}
		npc.setDroppedExp(XP);

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

	@Override
	public boolean isMyNpc(Entity entity) {
		return super.isNPC(entity);
	}
}
