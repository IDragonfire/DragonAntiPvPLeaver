package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.EnumGamemode;
import net.minecraft.server.v1_5_R2.PlayerInteractManager;
import net.minecraft.server.v1_5_R2.World;
import net.minecraft.server.v1_5_R2.WorldServer;

import org.bukkit.craftbukkit.v1_5_R2.entity.CraftEntity;

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
	public NPCEntity(World world, String s,
			PlayerInteractManager itemInWorldManager) {
		super(world.getServer().getServer(), (WorldServer) world, s,
				itemInWorldManager);

		itemInWorldManager.b(EnumGamemode.SURVIVAL);
		this.playerConnection = new NPCNetHandler(
				world.getServer().getServer(), this);
	}

	public void setBukkitEntity(org.bukkit.entity.Entity entity) {
		bukkitEntity = (CraftEntity) entity;
	}
}
