package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.MinecraftServer;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PlayerConnection;

/**
 * Bukkit:
 * "https://github.com/Bukkit/CraftBukkit/blob/master/src/main/java/net/minecraft/server/PlayerConnection.java"
 * caliog:
 * "https://github.com/caliog/NPCLib/blob/master/com/sharesc/caliog/npclib/NPCPlayerConnection.java"
 * Citiziens:
 * "https://github.com/CitizensDev/Citizens2/blob/master/src/main/java/net/citizensnpcs/npc/network/EmptyNetHandler.java"
 * Combat-Tag:
 * "https://github.com/cheddar262/Combat-Tag/blob/master/CombatTag/com/topcat/npclib/nms/NPCNetHandler.java"
 * Top-Cat:
 * "https://github.com/Top-Cat/NPCLib/blob/master/src/main/java/com/topcat/npclib/nms/NPCPlayerConnection.java"
 * lennis0012:
 * "https://github.com/lenis0012/NPCFactory/blob/master/src/main/java/com/lenis0012/bukkit/npc/NPCPlayerConnection.java"
 */
public class NPCNetHandler extends PlayerConnection {

	public NPCNetHandler(MinecraftServer mk_server, EntityPlayer entityplayer) {
		super(mk_server, new NPCNetworkManager(), entityplayer);
	}

	@Override
	public void sendPacket(Packet arg0) {
		// nothing
	}
}
