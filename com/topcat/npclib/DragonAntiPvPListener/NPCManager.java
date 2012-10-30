package com.topcat.npclib.DragonAntiPvPListener;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.server.Entity;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.topcat.npclib.DragonAntiPvPListener.entity.HumanNPC;
import com.topcat.npclib.DragonAntiPvPListener.entity.NPC;
import com.topcat.npclib.DragonAntiPvPListener.nms.BServer;
import com.topcat.npclib.DragonAntiPvPListener.nms.BWorld;
import com.topcat.npclib.DragonAntiPvPListener.nms.NPCEntity;
import com.topcat.npclib.DragonAntiPvPListener.nms.NPCNetworkManager;

/**
 * 
 * @author martin
 */
public class NPCManager {

    private HashMap<String, NPC> npcs = new HashMap<String, NPC>();
    private BServer server;
    private int taskid;
    private Map<World, BWorld> bworlds = new HashMap<World, BWorld>();
    private NPCNetworkManager npcNetworkManager;
    public static JavaPlugin plugin;

    public NPCManager(JavaPlugin plugin) {
        this.server = BServer.getInstance();

        try {
            this.npcNetworkManager = new NPCNetworkManager();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NPCManager.plugin = plugin;
        this.taskid = Bukkit.getServer().getScheduler()
                .scheduleSyncRepeatingTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        HashSet<String> toRemove = new HashSet<String>();
                        for (String i : NPCManager.this.npcs.keySet()) {
                            Entity j = NPCManager.this.npcs.get(i).getEntity();
                            j.y();
                            if (j.dead) {
                                toRemove.add(i);
                            }
                        }
                        for (String n : toRemove) {
                            NPCManager.this.npcs.remove(n);
                        }
                    }
                }, 1L, 1L);
        Bukkit.getServer().getPluginManager().registerEvents(new SL(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new WL(), plugin);
    }

    public BWorld getBWorld(World world) {
        BWorld bworld = this.bworlds.get(world);
        if (bworld != null) {
            return bworld;
        }
        bworld = new BWorld(world);
        this.bworlds.put(world, bworld);
        return bworld;
    }

    private class SL implements Listener {
        @SuppressWarnings("unused")
        @EventHandler
        public void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin() == plugin) {
                despawnAll();
                Bukkit.getServer().getScheduler().cancelTask(
                        NPCManager.this.taskid);
            }
        }
    }

    private class WL implements Listener {
        @SuppressWarnings("unused")
        @EventHandler
        public void onChunkLoad(ChunkLoadEvent event) {
            for (NPC npc : NPCManager.this.npcs.values()) {
                if (npc != null
                        && event.getChunk() == npc.getBukkitEntity()
                                .getLocation().getBlock().getChunk()) {
                    BWorld world = getBWorld(event.getWorld());
                    world.getWorldServer().addEntity(npc.getEntity());
                }
            }
        }
    }

    public NPC spawnHumanNPC(String name, Location l) {
        int i = 0;
        String id = name;
        while (this.npcs.containsKey(id)) {
            id = name + i;
            i++;
        }
        return spawnHumanNPC(name, l, id);
    }

    public NPC spawnHumanNPC(String name, Location l, String id) {
        if (this.npcs.containsKey(id)) {
            this.server.getLogger().log(Level.WARNING,
                    "NPC with that id already exists, existing NPC returned");
            return this.npcs.get(id);
        } else {
            if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
                String tmp = name.substring(0, 16);
                this.server.getLogger().log(Level.WARNING,
                        "NPCs can't have names longer than 16 characters,");
                this.server.getLogger().log(Level.WARNING,
                        name + " has been shortened to " + tmp);
                name = tmp;
            }
            BWorld world = getBWorld(l.getWorld());
            NPCEntity npcEntity = new NPCEntity(this, world, name,
                    new ItemInWorldManager(world.getWorldServer()));
            npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l
                    .getYaw(), l.getPitch());
            world.getWorldServer().addEntity(npcEntity); // the right way
            NPC npc = new HumanNPC(npcEntity);
            this.npcs.put(id, npc);
            return npc;
        }
    }

    public void despawnById(String id) {
        NPC npc = this.npcs.get(id);
        if (npc != null) {
            this.npcs.remove(id);
            npc.removeFromWorld();
        }
    }

    public void despawnHumanByName(String npcName) {
        if (npcName.length() > 16) {
            npcName = npcName.substring(0, 16); // Ensure you can still despawn
        }
        HashSet<String> toRemove = new HashSet<String>();
        for (String n : this.npcs.keySet()) {
            NPC npc = this.npcs.get(n);
            if (npc instanceof HumanNPC) {
                if (npc != null && ((HumanNPC) npc).getName().equals(npcName)) {
                    toRemove.add(n);
                    npc.removeFromWorld();
                }
            }
        }
        for (String n : toRemove) {
            this.npcs.remove(n);
        }
    }

    public void despawnAll() {
        for (NPC npc : this.npcs.values()) {
            if (npc != null) {
                npc.removeFromWorld();
            }
        }
        this.npcs.clear();
    }

    public NPC getNPC(String id) {
        return this.npcs.get(id);
    }

    public boolean isNPC(org.bukkit.entity.Entity e) {
        return ((CraftEntity) e).getHandle() instanceof NPCEntity;
    }

    public List<NPC> getHumanNPCByName(String name) {
        List<NPC> ret = new ArrayList<NPC>();
        Collection<NPC> i = this.npcs.values();
        for (NPC e : i) {
            if (e instanceof HumanNPC) {
                if (((HumanNPC) e).getName().equalsIgnoreCase(name)) {
                    ret.add(e);
                }
            }
        }
        return ret;
    }

    public List<NPC> getNPCs() {
        return new ArrayList<NPC>(this.npcs.values());
    }

    public String getNPCIdFromEntity(org.bukkit.entity.Entity e) {
        if (e instanceof HumanEntity) {
            for (String i : this.npcs.keySet()) {
                if (this.npcs.get(i).getBukkitEntity().getEntityId() == ((HumanEntity) e)
                        .getEntityId()) {
                    return i;
                }
            }
        }
        return null;
    }

    public void rename(String id, String name) {
        if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
            String tmp = name.substring(0, 16);
            this.server.getLogger().log(Level.WARNING,
                    "NPCs can't have names longer than 16 characters,");
            this.server.getLogger().log(Level.WARNING,
                    name + " has been shortened to " + tmp);
            name = tmp;
        }
        HumanNPC npc = (HumanNPC) getNPC(id);
        npc.setName(name);
        BWorld b = getBWorld(npc.getBukkitEntity().getLocation().getWorld());
        WorldServer s = b.getWorldServer();
        try {
            Method m = s.getClass().getDeclaredMethod("d",
                    new Class[] { Entity.class });
            m.setAccessible(true);
            m.invoke(s, npc.getEntity());
            m = s.getClass().getDeclaredMethod("c",
                    new Class[] { Entity.class });
            m.setAccessible(true);
            m.invoke(s, npc.getEntity());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        s.everyoneSleeping();
    }

    public BServer getServer() {
        return this.server;
    }

    public NPCNetworkManager getNPCNetworkManager() {
        return this.npcNetworkManager;
    }

}
