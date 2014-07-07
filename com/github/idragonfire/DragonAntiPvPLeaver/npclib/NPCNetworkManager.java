package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import javax.crypto.SecretKey;

import net.minecraft.server.v1_7_R1.EnumProtocol;
import net.minecraft.server.v1_7_R1.NetworkManager;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketListener;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.util.concurrent.GenericFutureListener;

public class NPCNetworkManager extends NetworkManager {

	public NPCNetworkManager() {
		super(false);
	}

	@Override
	public void a() {
		// nothing
	}

	@Override
	protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) {
		// nothing
	}

	@Override
	public void a(EnumProtocol enumprotocol) {
		// nothing
	}

	@Override
	public void a(PacketListener packetlistener) {
		// nothing
	}

	@Override
	public void a(SecretKey secretkey) {
		// nothing
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public void channelActive(ChannelHandlerContext channelhandlercontext)
			throws Exception {
		// nothing
	}

	@Override
	public void channelInactive(ChannelHandlerContext channelhandlercontext) {
		// nothing
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelhandlercontext,
			Object object) {
		// nothing
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1) {
		// nothing
	}

	@Override
	public void g() {
		// nothing
	}

	@Override
	public void handle(Packet packet,
			GenericFutureListener... agenericfuturelistener) {
		// nothing
	}

	@Override
	public boolean acceptInboundMessage(Object msg) throws Exception {
		return false;
	}

	@Override
	public void channelRead(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		// nothing
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// nothing
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// nothing
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		// nothing
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx)
			throws Exception {
		// nothing
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		// nothing
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		// nothing
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// nothing
	}

}
