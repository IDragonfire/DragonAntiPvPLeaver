package com.topcat.npclib.DragonAntiPvPListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.topcat.npclib.DragonAntiPvPListener.entity.HumanNPC;
import com.topcat.npclib.DragonAntiPvPListener.nms.BServer;
import com.topcat.npclib.DragonAntiPvPListener.nms.BWorld;
import com.topcat.npclib.DragonAntiPvPListener.nms.NPCEntity;
import com.topcat.npclib.DragonAntiPvPListener.nms.NPCNetworkManager;

/**
 * 
 * @author martin
 */
public class NPCManager {

	private HashMap<UUID, HumanNPC> npcs = new HashMap<UUID, HumanNPC>();
	private BServer server;
	private int taskid;
	private Map<World, BWorld> bworlds = new HashMap<World, BWorld>();
	private NPCNetworkManager npcNetworkManager;
	public static JavaPlugin plugin;

	public NPCManager(JavaPlugin plugin) {
		server = BServer.getInstance();

		try {
			npcNetworkManager = new NPCNetworkManager();
		} catch (IOException e) {
			e.printStackTrace();
		}

		NPCManager.plugin = plugin;
		taskid = Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(plugin, new Runnable() {
					@Override
					public void run() {
						HashSet<UUID> toRemove = new HashSet<UUID>();
						for (UUID i : npcs.keySet()) {
							Entity j = npcs.get(i).getEntity();
							// EntityBaseTickMethod
							j.B();
							if (j.dead) {
								toRemove.add(i);
							}
						}
						for (UUID n : toRemove) {
							npcs.remove(n);
						}
					}
				}, 1L, 1L);
		Bukkit.getServer().getPluginManager().registerEvents(new SL(), plugin);
		Bukkit.getServer().getPluginManager().registerEvents(new WL(), plugin);
	}

	public BWorld getBWorld(World world) {
		BWorld bworld = bworlds.get(world);
		if (bworld != null) {
			return bworld;
		}
		bworld = new BWorld(world);
		bworlds.put(world, bworld);
		return bworld;
	}

	private class SL implements Listener {
		@EventHandler
		public void onPluginDisable(PluginDisableEvent event) {
			if (event.getPlugin() == plugin) {
				despawnAll();
				Bukkit.getServer().getScheduler().cancelTask(taskid);
			}
		}
	}

	private class WL implements Listener {
		@EventHandler
		public void onChunkLoad(ChunkLoadEvent event) {
			for (HumanNPC npc : npcs.values()) {
				if (npc != null
						&& event.getChunk() == npc.getBukkitEntity()
								.getLocation().getBlock().getChunk()) {
					BWorld world = getBWorld(event.getWorld());
					world.getWorldServer().addEntity(npc.getEntity());
				}
			}
		}
	}

	public HumanNPC spawnHumanNPC(UUID uuid, Location l) {
		while (npcs.containsKey(uuid)) {
			server.getLogger().log(Level.WARNING,
					"NPC with that id already exists");
			return npcs.get(uuid);
		}
		return spawnHumanNPC(Bukkit.getOfflinePlayer(uuid).getName(), l, uuid);
	}

	public HumanNPC spawnHumanNPC(String name, Location l, UUID uuid) {
		if (npcs.containsKey(uuid)) {
			server.getLogger().log(Level.WARNING,
					"NPC with that id already exists, existing NPC returned");
			return npcs.get(uuid);
		} else {
			if (name.length() > 16) { // Check and nag if name is too long,
										// spawn NPC anyway with shortened name.
				String tmp = name.substring(0, 16);
				server.getLogger().log(Level.WARNING,
						"NPCs can't have names longer than 16 characters,");
				server.getLogger().log(Level.WARNING,
						name + " has been shortened to " + tmp);
				name = tmp;
			}
			BWorld world = getBWorld(l.getWorld());
			NPCEntity npcEntity = new NPCEntity(this, world, new GameProfile(
					uuid, name), new PlayerInteractManager(
					world.getWorldServer()));
			npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(),
					l.getYaw(), l.getPitch());
			world.getWorldServer().addEntity(npcEntity); // the right way
			HumanNPC npc = new HumanNPC(npcEntity);
			npcs.put(uuid, npc);
			return npc;
		}
	}

	public void despawnById(String id) {
		HumanNPC npc = npcs.get(id);
		if (npc != null) {
			npcs.remove(id);
			npc.removeFromWorld();
		}
	}

	public void despawnHumanByName(String npcName) {
		if (npcName.length() > 16) {
			npcName = npcName.substring(0, 16); // Ensure you can still despawn
		}
		HashSet<UUID> toRemove = new HashSet<UUID>();
		for (UUID n : npcs.keySet()) {
			HumanNPC npc = npcs.get(n);
			if (npc instanceof HumanNPC) {
				if (npc != null && (npc).getName().equals(npcName)) {
					toRemove.add(n);
					npc.removeFromWorld();
				}
			}
		}
		for (UUID n : toRemove) {
			npcs.remove(n);
		}
	}

	public void despawnAll() {
		for (HumanNPC npc : npcs.values()) {
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
		List<HumanNPC> ret = new ArrayList<HumanNPC>();
		Collection<HumanNPC> i = npcs.values();
		for (HumanNPC e : i) {
			if (e instanceof HumanNPC) {
				if ((e).getName().equalsIgnoreCase(name)) {
					ret.add(e);
				}
			}
		}
		return ret;
	}

	public BServer getServer() {
		return server;
	}

	public NPCNetworkManager getNPCNetworkManager() {
		return npcNetworkManager;
	}

}
