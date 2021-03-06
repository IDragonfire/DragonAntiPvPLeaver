package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.World;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * caliog:
 * "https://github.com/caliog/NPCLib/blob/master/com/sharesc/caliog/npclib/NPCManager.java"
 * Combat-Tag:
 * "https://github.com/cheddar262/Combat-Tag/blob/master/CombatTag/com/topcat/npclib/NPCManager.java"
 * Top-Cat:
 * "https://github.com/Top-Cat/NPCLib/blob/master/src/main/java/com/topcat/npclib/NPCManager.java"
 * lennis0012:
 * "https://github.com/lenis0012/NPCFactory/blob/master/src/main/java/com/lenis0012/bukkit/npc/NPCFactory.java"
 */
public class NPCManager {

	private final HashMap<String, HumanNPC> npcs = new HashMap<>();
	public static JavaPlugin plugin;

	public NPCManager(JavaPlugin plugin) {

		NPCManager.plugin = plugin;

		Bukkit.getServer().getPluginManager().registerEvents(new SL(), plugin);
	}

	private class SL implements Listener {
		@EventHandler
		public void onPluginDisable(PluginDisableEvent event) {
			if (event.getPlugin() == plugin) {
				despawnAll();
			}
		}
	}

	public boolean containsNPC(String name) {
		return npcs.containsKey(name);
	}

	public HumanNPC spawnHumanNPC(String name, Location l) {
		int i = 0;
		String id = name;
		while (npcs.containsKey(id)) {
			id = name + i;
			i++;
		}
		return spawnHumanNPC(name, l, id);
	}

	public HumanNPC spawnHumanNPC(String name, Location l, String id) {
		if (npcs.containsKey(id)) {
			Bukkit.getLogger().log(Level.WARNING,
					"NPC with that id already exists, existing NPC returned");
			return npcs.get(id);
		}
		if (name.length() > 16) { // Check and nag if name is too long, spawn
									// NPC anyway with shortened name.
			final String tmp = name.substring(0, 16);
			Bukkit.getLogger().log(Level.WARNING,
					"NPCs can't have names longer than 16 characters,");
			Bukkit.getLogger().log(Level.WARNING,
					name + " has been shortened to " + tmp);
			name = tmp;
		}
		CraftWorld cworld = (CraftWorld) l.getWorld();
		World world = cworld.getHandle();
		GameProfile g = new GameProfile(UUID.randomUUID(), name);
		final NPCEntity npcEntity = new NPCEntity(world, g,
				new PlayerInteractManager(world));
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(),
				l.getPitch());
		world.addEntity(npcEntity); // the right way
		final HumanNPC npc = new HumanNPC(npcEntity);
		npcs.put(id, npc);
		return npc;
	}

	public void despawnById(String id) {
		final HumanNPC npc = npcs.get(id);
		if (npc != null) {
			npcs.remove(id);
			npc.removeFromWorld();
		}
	}

	public void despawnHumanByName(String npcName) {
		if (npcName.length() > 16) {
			npcName = npcName.substring(0, 16); // Ensure you can still despawn
		}
		final HashSet<String> toRemove = new HashSet<>();
		for (final String n : npcs.keySet()) {
			final HumanNPC npc = npcs.get(n);
			if (npc != null && npc instanceof HumanNPC) {
				if (((HumanNPC) npc).getName().equals(npcName)) {
					toRemove.add(n);
					npc.removeFromWorld();
				}
			}
		}
		for (final String n : toRemove) {
			npcs.remove(n);
		}
	}

	public void despawnAll() {
		for (final HumanNPC npc : npcs.values()) {
			if (npc != null) {
				npc.removeFromWorld();
			}
		}
		npcs.clear();
	}

	public HumanNPC getNPC(String id) {
		return npcs.get(id);
	}

	public boolean isNPC(org.bukkit.entity.Entity e) {
		return ((CraftEntity) e).getHandle() instanceof NPCEntity;
	}

	public List<HumanNPC> getHumanNPCByName(String name) {
		final List<HumanNPC> ret = new ArrayList<>();
		final Collection<HumanNPC> i = npcs.values();
		for (final HumanNPC e : i) {
			if (e instanceof HumanNPC) {
				if (((HumanNPC) e).getName().equalsIgnoreCase(name)) {
					ret.add(e);
				}
			}
		}
		return ret;
	}
}
