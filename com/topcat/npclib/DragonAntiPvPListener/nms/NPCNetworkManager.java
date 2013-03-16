package com.topcat.npclib.DragonAntiPvPListener.nms;

import java.io.IOException;
import java.lang.reflect.Field;

import net.minecraft.server.v1_5_R1.Connection;
import net.minecraft.server.v1_5_R1.ConsoleLogManager;
import net.minecraft.server.v1_5_R1.NetworkManager;
import net.minecraft.server.v1_5_R1.Packet;

/**
 * 
 * @author martin
 */
public class NPCNetworkManager extends NetworkManager {

    public NPCNetworkManager() throws IOException {
        super(new ConsoleLogManager("NPC Manager", (String) null, (String) null),new NullSocket(), "NPC Manager", new Connection() {
            @Override
            public boolean a() {
                return true;
            }
        }, null);
        try {
            Field f = NetworkManager.class.getDeclaredField("N");
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
    public void a(String s, Object... aobject) {
    }

    @Override
    public void a() {
    }

}
