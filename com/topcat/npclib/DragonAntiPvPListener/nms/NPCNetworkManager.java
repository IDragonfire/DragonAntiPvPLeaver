package com.topcat.npclib.DragonAntiPvPListener.nms;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import javax.crypto.SecretKey;

import net.minecraft.server.v1_7_R3.EnumProtocol;
import net.minecraft.server.v1_7_R3.IChatBaseComponent;
import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.server.v1_7_R3.NetworkManager;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketListener;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.util.concurrent.GenericFutureListener;

/**
 * 
 * @author martin
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
	@Override
	public void handle(Packet packet,
			GenericFutureListener... agenericfuturelistener) {
	}
}
