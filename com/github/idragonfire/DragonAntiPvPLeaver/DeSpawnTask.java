package com.github.idragonfire.DragonAntiPvPLeaver;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DFakePlayerManager;

public class DeSpawnTask implements Runnable {
    private Plugin plugin;
    private String npcID;
    private DFakePlayerManager nm;
    private long increase;

    public void increaseTime(long time) {
        increase = time;
    }

    public DeSpawnTask(String npcID, DFakePlayerManager nm, Plugin plugin) {
        this.plugin = plugin;
        this.npcID = npcID;
        this.nm = nm;
        increase = 0;
    }

    public void run() {
        if (increase > 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    new DeSpawnTask(npcID, nm, plugin), increase);
        } else {
            nm.despawnHumanByName(npcID);
        }
    }
}
