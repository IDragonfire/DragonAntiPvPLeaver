package com.topcat.npclib.DragonAntiPvPListener.nms;

import net.minecraft.server.v1_4_5.Entity;
import net.minecraft.server.v1_4_5.EntityHuman;
import net.minecraft.server.v1_4_5.EntityPlayer;
import net.minecraft.server.v1_4_5.EnumGamemode;
import net.minecraft.server.v1_4_5.ItemInWorldManager;
import net.minecraft.server.v1_4_5.WorldServer;

import org.bukkit.craftbukkit.v1_4_5.CraftServer;
import org.bukkit.event.entity.EntityTargetEvent;

import com.topcat.npclib.DragonAntiPvPListener.NPCManager;

/**
 * 
 * @author martin
 */
public class NPCEntity extends EntityPlayer {

    private int lastTargetId;
    private long lastBounceTick;
    private int lastBounceId;

    public NPCEntity(NPCManager npcManager, BWorld world, String s,
            ItemInWorldManager itemInWorldManager) {
        super(npcManager.getServer().getMCServer(), world.getWorldServer(), s,
                itemInWorldManager);

        itemInWorldManager.b(EnumGamemode.SURVIVAL);

        this.netServerHandler = new NPCNetHandler(npcManager, this);
        this.lastTargetId = -1;
        this.lastBounceId = -1;
        this.lastBounceTick = 0;

        this.fauxSleeping = true;
    }

    public void setBukkitEntity(org.bukkit.entity.Entity entity) {
        this.bukkitEntity = entity;
    }

    @Override
    public boolean a(EntityHuman entity) {
        EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(),
                entity.getBukkitEntity(),
                NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
        CraftServer server = ((WorldServer) this.world).getServer();
        server.getPluginManager().callEvent(event);

        return super.a(entity);
    }

    @Override
    public void c_(EntityHuman entity) {
        if ((this.lastBounceId != entity.id || System.currentTimeMillis()
                - this.lastBounceTick > 1000)
                && entity.getBukkitEntity().getLocation().distanceSquared(
                        getBukkitEntity().getLocation()) <= 1) {
            EntityTargetEvent event = new NpcEntityTargetEvent(
                    getBukkitEntity(), entity.getBukkitEntity(),
                    NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
            CraftServer server = ((WorldServer) this.world).getServer();
            server.getPluginManager().callEvent(event);

            this.lastBounceTick = System.currentTimeMillis();
            this.lastBounceId = entity.id;
        }

        if (this.lastTargetId == -1 || this.lastTargetId != entity.id) {
            EntityTargetEvent event = new NpcEntityTargetEvent(
                    getBukkitEntity(), entity.getBukkitEntity(),
                    NpcEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
            CraftServer server = ((WorldServer) this.world).getServer();
            server.getPluginManager().callEvent(event);
            this.lastTargetId = entity.id;
        }

        super.c_(entity);
    }

    @Override
    public void c(Entity entity) {
        if (this.lastBounceId != entity.id
                || System.currentTimeMillis() - this.lastBounceTick > 1000) {
            EntityTargetEvent event = new NpcEntityTargetEvent(
                    getBukkitEntity(), entity.getBukkitEntity(),
                    NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
            CraftServer server = ((WorldServer) this.world).getServer();
            server.getPluginManager().callEvent(event);

            this.lastBounceTick = System.currentTimeMillis();
        }

        this.lastBounceId = entity.id;

        super.c(entity);
    }

    @Override
    public void move(double arg0, double arg1, double arg2) {
        setPosition(arg0, arg1, arg2);
    }

}