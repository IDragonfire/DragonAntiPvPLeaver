package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import java.net.SocketAddress;

import net.minecraft.server.v1_5_R3.Connection;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.INetworkManager;
import net.minecraft.server.v1_5_R3.MinecraftServer;
import net.minecraft.server.v1_5_R3.Packet;
import net.minecraft.server.v1_5_R3.PlayerConnection;

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
		super(mk_server, new INetworkManager() {

			@Override
			public void a() {
				// TODO Auto-generated method stub

			}

			@Override
			public void a(Connection arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void a(String arg0, Object... arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void b() {
				// TODO Auto-generated method stub

			}

			@Override
			public void d() {
				// TODO Auto-generated method stub

			}

			@Override
			public int e() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public SocketAddress getSocketAddress() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void queue(Packet arg0) {
				// TODO Auto-generated method stub

			}
		}, entityplayer);
	}

	@Override
	public void sendPacket(Packet arg0) {
		// nothing
	}
}
