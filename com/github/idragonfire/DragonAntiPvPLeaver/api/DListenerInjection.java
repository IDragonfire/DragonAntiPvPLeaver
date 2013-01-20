package com.github.idragonfire.DragonAntiPvPLeaver.api;

import org.bukkit.event.entity.EntityDamageEvent;

public interface DListenerInjection {
    public void onEntityDamageByEntity(EntityDamageEvent event);
}
