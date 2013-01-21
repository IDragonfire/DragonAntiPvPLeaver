package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.HashMap;
import java.util.Hashtable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.idragonfire.DragonAntiPvPLeaver.DamageTrackerConfig;
import com.github.idragonfire.DragonAntiPvPLeaver.Plugin.DAMAGE_MODE;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DEntityDamageByEntityListenerInjection;

public abstract class TimeListenerInjection implements
        DEntityDamageByEntityListenerInjection {
    protected HashMap<DAMAGE_MODE, DamageTrackerConfig> mode;
    protected Hashtable<String, Long> timeTable;

    public TimeListenerInjection(HashMap<DAMAGE_MODE, DamageTrackerConfig> mode) {
        this.mode = mode;
        timeTable = new Hashtable<String, Long>();
    }

    public abstract void onEntityDamageByEntity(LivingEntity attacker,
            Entity victim);

    public boolean activeCooldown(String name) {
        if (timeTable.containsKey(name)) {
            return System.currentTimeMillis() < timeTable.get(name);
        }
        return false;
    }

    public void onEntityDamageByEntity(EntityDamageEvent event) {
        LivingEntity attacker = getAttacker(event);
        if (attacker == null || event.getEntity() == null) {
            return;
        }
        onEntityDamageByEntity(attacker, event.getEntity());
    }

    private LivingEntity getAttacker(EntityDamageEvent event) {
        if (event == null) {
            return null;
        }
        if ((event instanceof EntityDamageByEntityEvent)) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if ((damager instanceof Player)) {
                return (LivingEntity) damager;
            }
            if ((damager instanceof Projectile)) {
                Projectile projectile = (Projectile) damager;
                if ((projectile.getShooter() instanceof Player)) {
                    return projectile.getShooter();
                }
            } else if ((damager instanceof Tameable)) {
                Tameable tamed = (Tameable) damager;
                if ((tamed.isTamed())
                        && ((tamed.getOwner() instanceof LivingEntity))) {
                    return (LivingEntity) tamed.getOwner();
                }
            }
        }
        return null;
    }
}
