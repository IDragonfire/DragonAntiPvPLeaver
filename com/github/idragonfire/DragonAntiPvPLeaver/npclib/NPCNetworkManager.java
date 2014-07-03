package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import java.io.IOException;
import java.lang.reflect.Field;

import net.minecraft.server.v1_6_R3.Connection;
import net.minecraft.server.v1_6_R3.NetworkManager;
import net.minecraft.server.v1_6_R3.Packet;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;

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
		super(((CraftServer) Bukkit.getServer()).getServer().getLogger(),
				new NullSocket(), "NPC Manager", new Connection() {
					@Override
					public boolean a() {
						return true;
					}
				}, null);

		try {
			final Field f = NetworkManager.class.getDeclaredField("n");
			f.setAccessible(true);
			f.set(this, false);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void a(Connection nethandler) {
	}

	@Override
	public void queue(Packet packet) {
	}

	@Override
	public void a(String s, Object... aobject) {
	}

	@Override
	public void a() {
	}

}