package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import com.github.idragonfire.DragonAntiPvPLeaver.DamageTrackerConfig;
import com.github.idragonfire.DragonAntiPvPLeaver.Plugin.DAMAGE_MODE;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DamageCooldownListenerTemplate;

public class UnderAttack extends DamageCooldownListenerTemplate {
    public UnderAttack(HashMap<DAMAGE_MODE, DamageTrackerConfig> mode) {
        super(mode);
    }

    @Override
    public void onEntityDamageByEntity(LivingEntity attacker, Entity victim) {
        if (victim instanceof HumanEntity) {
            checkEntityType(attacker, ((HumanEntity) victim).getName());
        }

    }
}
