package com.github.idragonfire.DragonAntiPvPLeaver;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class DeSpawnTask implements Runnable {
    private Plugin plugin;
    private String npcID;
    private DNPCManager nm;
    private long increase;

    public void increaseTime(long time) {
        this.increase = time;
    }

    public DeSpawnTask(String npcID, DNPCManager nm, Plugin plugin) {
        this.plugin = plugin;
        this.npcID = npcID;
        this.nm = nm;
        this.increase = 0;
    }

    public void run() {
        if (this.increase > 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin,
                    new DeSpawnTask(this.npcID, this.nm, this.plugin),
                    this.increase);
        } else {
            this.nm.despawnPlayerNPC(this.npcID);
        }
    }
}