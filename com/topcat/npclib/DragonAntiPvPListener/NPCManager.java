package com.topcat.npclib.DragonAntiPvPListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.server.v1_6_R2.Entity;
import net.minecraft.server.v1_6_R2.PlayerInteractManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;
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

    private HashMap<String, HumanNPC> npcs = new HashMap<String, HumanNPC>();
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
        taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
                plugin, new Runnable() {
                    @Override
                    public void run() {
                        HashSet<String> toRemove = new HashSet<String>();
                        for (String i : npcs.keySet()) {
                            Entity j = npcs.get(i).getEntity();
                            j.x();
                            if (j.dead) {
                                toRemove.add(i);
                            }
                        }
                        for (String n : toRemove) {
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
            server.getLogger().log(Level.WARNING,
                    "NPC with that id already exists, existing NPC returned");
            return npcs.get(id);
        } else {
            if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
                String tmp = name.substring(0, 16);
                server.getLogger().log(Level.WARNING,
                        "NPCs can't have names longer than 16 characters,");
                server.getLogger().log(Level.WARNING,
                        name + " has been shortened to " + tmp);
                name = tmp;
            }
            BWorld world = getBWorld(l.getWorld());
            NPCEntity npcEntity = new NPCEntity(this, world, name,
                    new PlayerInteractManager(world.getWorldServer()));
            npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l
                    .getYaw(), l.getPitch());
            world.getWorldServer().addEntity(npcEntity); // the right way
            HumanNPC npc = new HumanNPC(npcEntity);
            npcs.put(id, npc);
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
        HashSet<String> toRemove = new HashSet<String>();
        for (String n : npcs.keySet()) {
            HumanNPC npc = npcs.get(n);
            if (npc instanceof HumanNPC) {
                if (npc != null && (npc).getName().equals(npcName)) {
                    toRemove.add(n);
                    npc.removeFromWorld();
                }
            }
        }
        for (String n : toRemove) {
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
