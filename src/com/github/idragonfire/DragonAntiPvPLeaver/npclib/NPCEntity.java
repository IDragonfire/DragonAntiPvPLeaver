package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EnumGamemode;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.World;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;

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
	public NPCEntity(World world, GameProfile g,
			PlayerInteractManager itemInWorldManager) {
		super(world.getServer().getServer(), (WorldServer) world, g,
				itemInWorldManager);

		itemInWorldManager.b(EnumGamemode.SURVIVAL);
		this.playerConnection = new NPCNetHandler(
				world.getServer().getServer(), this);
	}

	public void setBukkitEntity(org.bukkit.entity.Entity entity) {
		bukkitEntity = (CraftEntity) entity;
	}
}
