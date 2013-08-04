package net.minecraft.server;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class DAPL_Injection {
    public static boolean nmsDisconnectCall(Object playerConnection) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(
                "DragonAntiPvPLeaver");
        if (plugin != null) {
            try {
                Method m = plugin.getClass().getDeclaredMethod("nmsDisconnectCall",
                        Object.class);
                return (Boolean) m.invoke(plugin, playerConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
