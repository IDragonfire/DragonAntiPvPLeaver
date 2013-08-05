package com.topcat.npclib.DragonAntiPvPListener.nms;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import net.minecraft.server.v1_6_R2.Connection;
import net.minecraft.server.v1_6_R2.MinecraftServer;
import net.minecraft.server.v1_6_R2.NetworkManager;
import net.minecraft.server.v1_6_R2.Packet;

/**
 * 
 * @author martin
 */
public class NPCNetworkManager extends NetworkManager {

    public NPCNetworkManager() throws IOException {
        super(MinecraftServer.getServer().getLogger(), new NullSocket(),
                "NPC Manager", new Connection() {

                    @Override
                    public boolean a() {
                        return true;
                    }
                }, null);
        try {
            Field f = NetworkManager.class.getDeclaredField("n");
            f.setAccessible(true);
            f.set(this, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void a(Connection connection) {
    }

    @Override
    public void queue(Packet packet) {
    }

    @Override
    public void a() {
    }

    @Override
    public void a(String s, Object... aobject) {
    }

    @Override
    public SocketAddress getSocketAddress() {
        return new InetSocketAddress(1);
    }
}
