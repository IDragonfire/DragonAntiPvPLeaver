package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import com.github.idragonfire.DragonAntiPvPLeaver.DAntiPvPLeaverPlugin;
import com.github.idragonfire.DragonAntiPvPLeaver.DAntiPvPLeaverPlugin.DAMAGE_MODE;

public class DTakeDamage extends DListenerInjection {

    public DTakeDamage(HashMap<DAMAGE_MODE, Integer> mode) {
        super(mode);
    }

    @Override
    public void onEntityDamageByEntity(LivingEntity attacker, Entity victim) {
        if (victim instanceof HumanEntity) {
            super.timeTable.put(((HumanEntity) victim).getName(),
                    DAntiPvPLeaverPlugin.checkEntityType(attacker, this.mode));
        }
    }
}
