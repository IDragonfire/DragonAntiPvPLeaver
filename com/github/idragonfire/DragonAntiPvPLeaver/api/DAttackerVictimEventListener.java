package com.github.idragonfire.DragonAntiPvPLeaver.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public interface DAttackerVictimEventListener {
    public void onEntityDamageByEntity(LivingEntity attacker, Entity victim);
}
