package com.github.idragonfire.DragonAntiPvPLeaver;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.topcat.npclib.DragonAntiPvPListener.NPCManager;

public class DeSpawnTask implements Runnable {
    private Plugin plugin;
    private String npcName;
    private NPCManager nm;
    private long increase;

    public void increaseTime(long time) {
        this.increase = time;
    }

    public DeSpawnTask(String npcName, NPCManager nm, Plugin plugin) {
        this.plugin = plugin;
        this.npcName = npcName;
        this.nm = nm;
        this.increase = 0;
    }

    public void run() {
        if (this.increase > 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin,
                    new DeSpawnTask(this.npcName, this.nm, this.plugin),
                    this.increase);
        } else {
            this.nm.despawnHumanByName(this.npcName);
        }
    }
}
