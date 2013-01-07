package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.Hashtable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class DPlayerTakeDamage implements DListenerInjection {

    private Hashtable<String, Long> victimTable;
    private long maxDamageTime;

    public DPlayerTakeDamage(long maxDamageTime) {
        this.maxDamageTime = maxDamageTime;
        this.victimTable = new Hashtable<String, Long>();
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (e instanceof HumanEntity) {
            HumanEntity h = (HumanEntity) e;
            victimTable.put(h.getName(), Long.valueOf(System
                    .currentTimeMillis()));
        }
    }

    @Override
    public boolean canDragonNpcSpawn(String name) {
        if (this.victimTable.containsKey(name)) {
            return System.currentTimeMillis() - this.victimTable.get(name) <= maxDamageTime;
        }
        return true;
    }
}
