package com.topcat.npclib.DragonAntiPvPListener.nms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.WorldProvider;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author martin
 */
public class BWorld {

    private BServer server;
    private World world;
    private CraftWorld cWorld;
    private WorldServer wServer;
    private WorldProvider wProvider;

    public BWorld(BServer server, String worldName) {
        this.server = server;
        this.world = server.getServer().getWorld(worldName);
        try {
            this.cWorld = (CraftWorld) this.world;
            this.wServer = this.cWorld.getHandle();
            this.wProvider = this.wServer.worldProvider;
        } catch (Exception ex) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
        }
    }

    public BWorld(World world) {
        this.world = world;
        try {
            this.cWorld = (CraftWorld) world;
            this.wServer = this.cWorld.getHandle();
            this.wProvider = this.wServer.worldProvider;
        } catch (Exception ex) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
        }
    }

    public PlayerManager getPlayerManager() {
        return this.wServer.getPlayerManager();
    }

    public CraftWorld getCraftWorld() {
        return this.cWorld;
    }

    public WorldServer getWorldServer() {
        return this.wServer;
    }

    public WorldProvider getWorldProvider() {
        return this.wProvider;
    }

    public boolean createExplosion(double x, double y, double z, float power) {
        return this.wServer.explode(null, x, y, z, power).wasCanceled ? false
                : true;
    }

    public boolean createExplosion(Location l, float power) {
        return this.wServer.explode(null, l.getX(), l.getY(), l.getZ(), power).wasCanceled ? false
                : true;
    }

    @SuppressWarnings("unchecked")
    public void removeEntity(String name, final Player player, JavaPlugin plugin) {
        this.server.getServer().getScheduler().callSyncMethod(plugin,
                new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        Location loc = player.getLocation();
                        CraftWorld craftWorld = (CraftWorld) player.getWorld();
                        CraftPlayer craftPlayer = (CraftPlayer) player;

                        double x = loc.getX() + 0.5;
                        double y = loc.getY() + 0.5;
                        double z = loc.getZ() + 0.5;
                        double radius = 10;

                        List<Entity> entities = new ArrayList<Entity>();
                        AxisAlignedBB bb = AxisAlignedBB.a(x - radius, y
                                - radius, z - radius, x + radius, y + radius, z
                                + radius);
                        entities = craftWorld.getHandle().getEntities(
                                craftPlayer.getHandle(), bb);
                        for (Entity o : entities) {
                            if (!(o instanceof EntityPlayer)) {
                                o.getBukkitEntity().remove();
                            }
                        }
                        return null;
                    }
                });
    }

}