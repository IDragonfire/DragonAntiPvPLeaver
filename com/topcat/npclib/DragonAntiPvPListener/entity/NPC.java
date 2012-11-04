package com.topcat.npclib.DragonAntiPvPListener.entity;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.topcat.npclib.DragonAntiPvPListener.NPCManager;
import com.topcat.npclib.DragonAntiPvPListener.pathing.NPCPath;
import com.topcat.npclib.DragonAntiPvPListener.pathing.NPCPathFinder;
import com.topcat.npclib.DragonAntiPvPListener.pathing.Node;
import com.topcat.npclib.DragonAntiPvPListener.pathing.PathReturn;

public class NPC {

    private Entity entity;
    private NPCPathFinder path;
    private Iterator<Node> pathIterator;
    private Node last;
    private NPCPath runningPath;
    private int taskid;
    private Runnable onFail;

    public NPC(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void removeFromWorld() {
        try {
            this.entity.world.removeEntity(this.entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public org.bukkit.entity.Entity getBukkitEntity() {
        return this.entity.getBukkitEntity();
    }

    public void moveTo(Location l) {
        getBukkitEntity().teleport(l);
    }

    public void pathFindTo(Location l, PathReturn callback) {
        pathFindTo(l, 3000, callback);
    }

    public void pathFindTo(Location l, int maxIterations, PathReturn callback) {
        if (this.path != null) {
            this.path.cancel = true;
        }
        if (l.getWorld() != getBukkitEntity().getWorld()) {
            ArrayList<Node> pathList = new ArrayList<Node>();
            pathList.add(new Node(l.getBlock()));
            callback.run(new NPCPath(null, pathList, l));
        } else {
            this.path = new NPCPathFinder(getBukkitEntity().getLocation(), l,
                    maxIterations, callback);
            this.path.start();
        }
    }

    public void walkTo(Location l) {
        walkTo(l, 3000);
    }

    public void walkTo(final Location l, final int maxIterations) {
        pathFindTo(l, maxIterations, new PathReturn() {
            @Override
            public void run(NPCPath path) {
                usePath(path, new Runnable() {
                    @Override
                    public void run() {
                        walkTo(l, maxIterations);
                    }
                });
            }
        });
    }

    public void usePath(NPCPath path) {
        usePath(path, new Runnable() {
            @Override
            public void run() {
                walkTo(NPC.this.runningPath.getEnd(), 3000);
            }
        });
    }

    public void usePath(NPCPath path, Runnable onFail) {
        if (this.taskid == 0) {
            this.taskid = Bukkit.getServer().getScheduler()
                    .scheduleSyncRepeatingTask(NPCManager.plugin,
                            new Runnable() {
                                @Override
                                public void run() {
                                    pathStep();
                                }
                            }, 6L, 6L);
        }
        this.pathIterator = path.getPath().iterator();
        this.runningPath = path;
        this.onFail = onFail;
    }

    private void pathStep() {
        if (this.pathIterator.hasNext()) {
            Node n = this.pathIterator.next();
            if (n.b.getWorld() != getBukkitEntity().getWorld()) {
                getBukkitEntity().teleport(n.b.getLocation());
            } else {
                float angle = getEntity().yaw;
                float look = getEntity().pitch;
                if (this.last == null
                        || this.runningPath.checkPath(n, this.last, true)) {
                    if (this.last != null) {
                        angle = (float) Math.toDegrees(Math.atan2(this.last.b
                                .getX()
                                - n.b.getX(), n.b.getZ() - this.last.b.getZ()));
                        look = (float) (Math.toDegrees(Math.asin(this.last.b
                                .getY()
                                - n.b.getY())) / 2);
                    }
                    getEntity().setPositionRotation(n.b.getX() + 0.5,
                            n.b.getY(), n.b.getZ() + 0.5, angle, look);
                    ((EntityPlayer) getEntity()).bS = angle;
                } else {
                    this.onFail.run();
                }
            }
            this.last = n;
        } else {
            getEntity().setPositionRotation(this.runningPath.getEnd().getX(),
                    this.runningPath.getEnd().getY(),
                    this.runningPath.getEnd().getZ(),
                    this.runningPath.getEnd().getYaw(),
                    this.runningPath.getEnd().getPitch());

            ((EntityPlayer) getEntity()).bS = this.runningPath.getEnd()
                    .getYaw();
            Bukkit.getServer().getScheduler().cancelTask(this.taskid);
            this.taskid = 0;
        }
    }

}