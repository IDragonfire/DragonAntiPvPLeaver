package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import org.bukkit.event.entity.EntityDamageEvent;

public interface DListenerInjection {
    public boolean canDragonNpcSpawn(String name);
    
    public void onEntityDamageByEntity(EntityDamageEvent event);
}
