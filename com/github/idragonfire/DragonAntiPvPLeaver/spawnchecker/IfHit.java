package com.github.idragonfire.DragonAntiPvPLeaver.spawnchecker;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import com.github.idragonfire.DragonAntiPvPLeaver.DamageTrackerConfig;
import com.github.idragonfire.DragonAntiPvPLeaver.DAPL_Plugin.DAMAGE_MODE;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DamageCooldownListenerTemplate;

public class IfHit extends DamageCooldownListenerTemplate {
    public IfHit(HashMap<DAMAGE_MODE, DamageTrackerConfig> mode) {
        super(mode);
    }

    @Override
    public void onEntityDamageByEntity(LivingEntity attacker, Entity victim) {
        if (attacker instanceof HumanEntity) {
            checkEntityType(victim, ((HumanEntity) attacker).getName());
        }
    }
}
