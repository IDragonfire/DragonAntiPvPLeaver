package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import com.github.idragonfire.DragonAntiPvPLeaver.DamageTrackerConfig;
import com.github.idragonfire.DragonAntiPvPLeaver.Plugin;
import com.github.idragonfire.DragonAntiPvPLeaver.Plugin.DAMAGE_MODE;

public class TakeDamageListener extends TimeListenerInjection {

    public TakeDamageListener(HashMap<DAMAGE_MODE, DamageTrackerConfig> mode) {
        super(mode);
    }

    @Override
    public void onEntityDamageByEntity(LivingEntity attacker, Entity victim) {
        if (victim instanceof HumanEntity) {
            super.timeTable.put(((HumanEntity) victim).getName(), Plugin
                    .checkEntityType(attacker, mode));
        }
    }
}
