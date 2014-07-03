package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EnumProtocol;
import net.minecraft.server.v1_7_R3.IChatBaseComponent;
import net.minecraft.server.v1_7_R3.NetworkManager;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayInAbilities;
import net.minecraft.server.v1_7_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_7_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_7_R3.PacketPlayInChat;
import net.minecraft.server.v1_7_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_7_R3.PacketPlayInCloseWindow;
import net.minecraft.server.v1_7_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_7_R3.PacketPlayInEnchantItem;
import net.minecraft.server.v1_7_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_7_R3.PacketPlayInFlying;
import net.minecraft.server.v1_7_R3.PacketPlayInHeldItemSlot;
import net.minecraft.server.v1_7_R3.PacketPlayInKeepAlive;
import net.minecraft.server.v1_7_R3.PacketPlayInSetCreativeSlot;
import net.minecraft.server.v1_7_R3.PacketPlayInSettings;
import net.minecraft.server.v1_7_R3.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_7_R3.PacketPlayInTabComplete;
import net.minecraft.server.v1_7_R3.PacketPlayInTransaction;
import net.minecraft.server.v1_7_R3.PacketPlayInUpdateSign;
import net.minecraft.server.v1_7_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_7_R3.PacketPlayInWindowClick;
import net.minecraft.server.v1_7_R3.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;

public class NPCNetHandler extends PlayerConnection {

	public NPCNetHandler(NPCManager npcManager, EntityPlayer entityplayer) {
		super(npcManager.getServer().getMCServer(), npcManager
				.getNPCNetworkManager(), entityplayer);
	}

	@Override
	public CraftPlayer getPlayer() {
		return new CraftPlayer((CraftServer) Bukkit.getServer(), player); // Fake
																			// player
																			// prevents
																			// spout
																			// NPEs
	}

	@Override
	public void a() {
	}

	@Override
	public void a(double d0, double d1, double d2, float f, float f1) {
	}

	@Override
	public void a(EnumProtocol enumprotocol, EnumProtocol enumprotocol1) {
	}

	@Override
	public void a(IChatBaseComponent ichatbasecomponent) {
	}

	@Override
	public void a(PacketPlayInAbilities arg0) {
	}

	@Override
	public void a(PacketPlayInArmAnimation arg0) {
	}

	@Override
	public void a(PacketPlayInBlockDig arg0) {
	}

	@Override
	public void a(PacketPlayInBlockPlace arg0) {
	}

	@Override
	public void a(PacketPlayInChat arg0) {
	}

	@Override
	public void a(PacketPlayInClientCommand arg0) {
	}

	@Override
	public void a(PacketPlayInCloseWindow packetplayinclosewindow) {
	}

	@Override
	public void a(PacketPlayInCustomPayload arg0) {
	}

	@Override
	public void a(PacketPlayInEnchantItem packetplayinenchantitem) {
	}

	@Override
	public void a(PacketPlayInEntityAction arg0) {
	}

	@Override
	public void a(PacketPlayInFlying arg0) {
	}

	@Override
	public void a(PacketPlayInHeldItemSlot arg0) {
	}

	@Override
	public void a(PacketPlayInKeepAlive arg0) {
	}

	@Override
	public void a(PacketPlayInSetCreativeSlot arg0) {
	}

	@Override
	public void a(PacketPlayInSettings packetplayinsettings) {
	}

	@Override
	public void a(PacketPlayInSteerVehicle packetplayinsteervehicle) {
	}

	@Override
	public void a(PacketPlayInTabComplete arg0) {
	}

	@Override
	public void a(PacketPlayInTransaction packetplayintransaction) {
	}

	@Override
	public void a(PacketPlayInUpdateSign arg0) {
	}

	@Override
	public void a(PacketPlayInUseEntity arg0) {
	}

	@Override
	public void a(PacketPlayInWindowClick arg0) {
	}

	@Override
	public NetworkManager b() {
		return null;
	}

	@Override
	public void chat(String arg0, boolean arg1) {
	}

	@Override
	public void disconnect(String s) {
	}

	@Override
	public void sendPacket(Packet arg0) {
	}

	@Override
	public void teleport(Location dest) {
	}
}
