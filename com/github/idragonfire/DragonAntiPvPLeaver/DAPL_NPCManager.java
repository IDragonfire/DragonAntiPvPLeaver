package com.github.idragonfire.DragonAntiPvPLeaver;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.server.v1_4_R1.EntityHuman;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.api.DespawnReason;
import de.kumpelblase2.remoteentities.api.RemoteEntityType;
import de.kumpelblase2.remoteentities.api.thinking.DamageBehavior;
import de.kumpelblase2.remoteentities.api.thinking.Mind;
import de.kumpelblase2.remoteentities.api.thinking.goals.DesireFindNearestTarget;
import de.kumpelblase2.remoteentities.entities.RemotePlayer;
import de.kumpelblase2.remoteentities.entities.RemotePlayerEntity;

public class DAPL_NPCManager {

    private EntityManager npcManager;
    private HashMap<String, RemotePlayer> playerNPCs;
    private HashSet<Entity> bukkitEntities;
    private DAntiPvPLeaverPlugin plugin;

    public DAPL_NPCManager(EntityManager npcManager, DAntiPvPLeaverPlugin plugin) {
        this.npcManager = npcManager;
        this.playerNPCs = new HashMap<String, RemotePlayer>();
        this.bukkitEntities = new HashSet<Entity>();
        this.plugin = plugin;
    }

    public boolean isDragonNPC(Entity entity) {
        return this.bukkitEntities.contains(entity);
    }

    public void despawnPlayerNPC(String npcID) {
        if (this.playerNPCs.containsKey(npcID)) {
            this.playerNPCs.get(npcID).despawn(DespawnReason.CUSTOM);
        }
    }

    public String spawnPlayerNPC(Player player) {
        // TODO: ChatColor for NPC name?
        RemotePlayer remoteEntity = (RemotePlayer) this.npcManager
                .createNamedEntity(RemoteEntityType.Human,
                        player.getLocation(), ChatColor.RED + player.getName());
        Mind mind = remoteEntity.getMind();
        final String npcID = "DragonPlayerNPC_" + player.getName();
        mind.addBehaviour(new DamageBehavior(remoteEntity) {
            @Override
            public void onRemove() {
                System.out.println("npc dead 2");
                super.onRemove();
            }

            @Override
            public void onDamage(EntityDamageEvent event) {
                System.out.println("event");
                DAPL_NPCManager.this.plugin.npcFirstTimeAttacked(npcID);
            }
        });

        remoteEntity.getMind().addActionDesire(
                new DesireFindNearestTarget(remoteEntity, EntityHuman.class,
                        64f, false, 100), 1);

        RemotePlayerEntity remotePlayerEntity = (RemotePlayerEntity) remoteEntity
                .getHandle();
        // TODO: use kumpelblase function
//        remotePlayerEntity.setSameInventoryAs(player);

        this.playerNPCs.put(npcID, remoteEntity);
        this.bukkitEntities.add(remoteEntity.getBukkitEntity());

        return npcID;
    }
}
