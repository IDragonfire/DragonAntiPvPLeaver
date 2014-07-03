package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.EnumGamemode;
import net.minecraft.server.v1_6_R3.PlayerInteractManager;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Bukkit:
 * "https://github.com/Bukkit/CraftBukkit/blob/master/src/main/java/net/minecraft/server/EntityPlayer.java"
 * caliog:
 * "https://github.com/caliog/NPCLib/blob/master/com/sharesc/caliog/npclib/NPCEntity.java"
 * Citiziens:
 * "https://github.com/CitizensDev/Citizens2/blob/master/src/main/java/net/citizensnpcs/npc/entity/EntityHumanNPC.java"
 * Combat-Tag:
 * "https://github.com/cheddar262/Combat-Tag/blob/master/CombatTag/com/topcat/npclib/nms/NPCEntity.java"
 * Top-Cat:
 * "https://github.com/Top-Cat/NPCLib/blob/master/src/main/java/com/topcat/npclib/nms/NPCEntity.java"
 * lennis0012:
 * "https://github.com/lenis0012/NPCFactory/blob/master/src/main/java/com/lenis0012/bukkit/npc/NPCEntity.java"
 */
public class NPCEntity extends EntityPlayer {
	private int lastTargetId;
	private long lastBounceTick;
	private int lastBounceId;

	public NPCEntity(NPCManager npcManager, BWorld world, String s,
			PlayerInteractManager itemInWorldManager) {
		super(npcManager.getServer().getMCServer(), world.getWorldServer(), s,
				itemInWorldManager);

		itemInWorldManager.b(EnumGamemode.SURVIVAL);

		this.playerConnection = new NPCNetHandler(npcManager, this);
		this.lastTargetId = -1;
		this.lastBounceId = -1;
		this.lastBounceTick = 0;

		fauxSleeping = true;
	}

	public void setBukkitEntity(org.bukkit.entity.Entity entity) {
		bukkitEntity = (CraftEntity) entity;
	}

	// https://github.com/Bukkit/CraftBukkit/blob/master/src/main/java/net/minecraft/server/EntityPlayer.java#L457
	@Override
	public boolean a(EntityHuman entity) {
		final EntityTargetEvent event = new NpcEntityTargetEvent(
				getBukkitEntity(), entity.getBukkitEntity(),
				NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
		Bukkit.getPluginManager().callEvent(event);

		return super.a(entity);
	}

	// https://github.com/Bukkit/CraftBukkit/blob/master/src/main/java/net/minecraft/server/Entity.java#L996
	public void b_(EntityHuman entity) {
		if ((lastBounceId != entity.id || System.currentTimeMillis()
				- lastBounceTick > 1000)
				&& entity.getBukkitEntity().getLocation()
						.distanceSquared(getBukkitEntity().getLocation()) <= 1) {
			final EntityTargetEvent event = new NpcEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
			Bukkit.getPluginManager().callEvent(event);

			lastBounceTick = System.currentTimeMillis();
			lastBounceId = entity.id;
		}

		if (lastTargetId == -1 || lastTargetId != entity.id) {
			final EntityTargetEvent event = new NpcEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NpcEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
			Bukkit.getPluginManager().callEvent(event);
			lastTargetId = entity.id;
		}

		super.b_(entity);
	}

	// https://github.com/Bukkit/CraftBukkit/blob/master/src/main/java/net/minecraft/server/EntityPlayer.java#L926
	@Override
	public void c(Entity entity) {
		if (lastBounceId != entity.id
				|| System.currentTimeMillis() - lastBounceTick > 1000) {
			final EntityTargetEvent event = new NpcEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
			Bukkit.getPluginManager().callEvent(event);

			lastBounceTick = System.currentTimeMillis();
		}

		lastBounceId = entity.id;

		super.c(entity);
	}

	@Override
	public void move(double arg0, double arg1, double arg2) {
		setPosition(arg0, arg1, arg2);
	}
}