package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import com.github.idragonfire.DragonAntiPvPLeaver.DAntiPvPLeaverPlugin;
import com.github.idragonfire.DragonAntiPvPLeaver.DAntiPvPLeaverPlugin.DAMAGE_MODE;

public class DDealDamage extends DamageTimeListenerInjection {

    public DDealDamage(HashMap<DAMAGE_MODE, Integer> mode) {
        super(mode);
    }

    @Override
    public void onEntityDamageByEntity(LivingEntity attacker, Entity victim) {
        if (attacker instanceof HumanEntity) {
            super.timeTable.put(((HumanEntity) attacker).getName(),
                    DAntiPvPLeaverPlugin.checkEntityType(victim, this.mode));
        }
    }
}
