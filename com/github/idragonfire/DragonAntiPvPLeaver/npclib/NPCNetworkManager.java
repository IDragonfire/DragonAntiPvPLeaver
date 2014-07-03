package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import java.io.IOException;

import javax.crypto.SecretKey;

import net.minecraft.server.v1_7_R3.EnumProtocol;
import net.minecraft.server.v1_7_R3.IChatBaseComponent;
import net.minecraft.server.v1_7_R3.NetworkManager;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketListener;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.util.concurrent.GenericFutureListener;

/**
 * Bukkit:
 * "https://github.com/Bukkit/CraftBukkit/blob/master/src/main/java/net/minecraft/server/NetworkManager.java"
 * caliog:
 * "https://github.com/caliog/NPCLib/blob/master/com/sharesc/caliog/npclib/NPCNetworkManager.java"
 * Citiziens:
 * "https://github.com/CitizensDev/Citizens2/blob/master/src/main/java/net/citizensnpcs/npc/network/EmptyNetworkManager.java"
 * Combat-Tag:
 * "https://github.com/cheddar262/Combat-Tag/blob/master/CombatTag/com/topcat/npclib/nms/NPCNetworkManager.java"
 * Top-Cat:
 * "https://github.com/Top-Cat/NPCLib/blob/master/src/main/java/com/topcat/npclib/nms/NPCNetworkManager.java"
 * lennis0012:
 * "https://github.com/lenis0012/NPCFactory/blob/master/src/main/java/com/lenis0012/bukkit/npc/NPCNetworkManager.java"
 */
public class NPCNetworkManager extends NetworkManager {

	public NPCNetworkManager() throws IOException {
		super(false);
	}

	@Override
	public void a() {
	}

	@Override
	protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) {
	}

	@Override
	public void a(EnumProtocol enumprotocol) {
	}

	@Override
	public void a(PacketListener packetlistener) {
	}

	@Override
	public void a(SecretKey secretkey) {
	}

	@Override
	public void channelActive(ChannelHandlerContext channelhandlercontext)
			throws Exception {
	}

	@Override
	public void channelInactive(ChannelHandlerContext channelhandlercontext) {
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelhandlercontext,
			Object object) {
	}

	@Override
	public void close(IChatBaseComponent ichatbasecomponent) {
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1) {
	}

	@Override
	public void g() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Packet packet,
			GenericFutureListener... agenericfuturelistener) {
	}
}
