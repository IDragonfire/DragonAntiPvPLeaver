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

import com.github.idragonfire.DragonAntiPvPLeaver.DAntiPvPLeaverPlugin.DAMAGE_MODE;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DListenerInjection;

public abstract class DamageTimeListenerInjection implements DListenerInjection {
    protected HashMap<DAMAGE_MODE, Integer> mode;
    protected Hashtable<String, Long> timeTable;

    public DamageTimeListenerInjection(HashMap<DAMAGE_MODE, Integer> mode) {
        this.mode = mode;
        this.timeTable = new Hashtable<String, Long>();
    }

    public abstract void onEntityDamageByEntity(LivingEntity attacker,
            Entity victim);

    public boolean canDragonNpcSpawn(String name) {
        if (this.timeTable.containsKey(name)) {
            return System.currentTimeMillis() > this.timeTable.get(name);
        }
        return true;
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
