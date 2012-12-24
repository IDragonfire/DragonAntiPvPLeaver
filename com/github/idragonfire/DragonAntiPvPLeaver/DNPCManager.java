package com.github.idragonfire.DragonAntiPvPLeaver;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.api.DespawnReason;
import de.kumpelblase2.remoteentities.api.RemoteEntityType;
import de.kumpelblase2.remoteentities.entities.RemotePlayer;

public class DNPCManager {

    private EntityManager npcManager;
    private HashMap<String, RemotePlayer> playerNPCs;
    private HashSet<Entity> bukkitEntities;

    public DNPCManager(EntityManager npcManager) {
        this.npcManager = npcManager;
        this.playerNPCs = new HashMap<String, RemotePlayer>();
        this.bukkitEntities = new HashSet<Entity>();
    }

    public boolean isDragonNPC(Entity entity) {
        return this.bukkitEntities.contains(entity);
    }

    public void despawnPlayerNPC(String npcID) {
        if (this.playerNPCs.containsKey(npcID)) {
            this.playerNPCs.get(npcID).despawn(DespawnReason.CUSTOM);
        }
    }

    public String spawnPlayerNPC(Player player, Location loc) {
        // // TODO: ChatColor for NPC name?
        // // HumanNPC npc = (HumanNPC) this.npcManager.spawnHumanNPC(
        // // playerNameToNpcName(name), loc);
        // ItemStack[] invContents = player.getInventory().getContents();
        // ItemStack[] armourContents = player.getInventory().getArmorContents();
        // npc.getInventory().setContents(invContents);
        // npc.getInventory().setArmorContents(armourContents);
        //
        // // Formula for calculating dropped XP
        // int XP = player.getLevel() * 7;
        // if (XP > 100) {
        // XP = 100;
        // }
        // npc.setDroppedExp(XP);
        //

        RemotePlayer entity = (RemotePlayer) this.npcManager.createNamedEntity(
                RemoteEntityType.Human, loc, player.getName());
        String npcID = "DragonPlayerNPC_" + player.getName();
        this.playerNPCs.put(npcID, entity);
        this.bukkitEntities.add(entity.getBukkitEntity());
        // entity.ge
        // TamingFeature feature = new RemoteTamingFeature(entity);
        // feature.tame(inEvent.getPlayer());
        // entity.getFeatures().addFeature(feature);
        // entity.getMind().addMovementDesire(
        // new DesireFollowTamer(entity, 5, 15),
        // entity.getMind().getHighestMovementPriority() + 1);
        return npcID;
    }
}
