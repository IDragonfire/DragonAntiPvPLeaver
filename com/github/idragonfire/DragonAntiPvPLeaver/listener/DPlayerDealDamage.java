package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.Hashtable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DPlayerDealDamage implements DListenerInjection {
    private Hashtable<String, Long> damageTable;
    private long maxDealTime;

    public DPlayerDealDamage(long maxDealTime) {
        this.maxDealTime = maxDealTime;
        this.damageTable = new Hashtable<String, Long>();
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageEvent event) {
        Entity e = getAttacker(event);
        if (e == null) {
            System.out.println("DPlayerDealDamage null");
            return;
        }
        if (e instanceof HumanEntity) {
            HumanEntity h = (HumanEntity) e;
            damageTable.put(h.getName(), Long.valueOf(System
                    .currentTimeMillis()));
        }
    }

    private Player getAttacker(EntityDamageEvent event) {
        if (event == null) {
            return null;
        }
        if ((event instanceof EntityDamageByEntityEvent)) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if ((damager instanceof Player)) {
                return (Player) damager;
            }
            if ((damager instanceof Projectile)) {
                Projectile projectile = (Projectile) damager;
                if ((projectile.getShooter() instanceof Player)) {
                    return (Player) projectile.getShooter();
                }
            } else if ((damager instanceof LivingEntity)) {
                if ((damager instanceof Tameable)) {
                    Tameable tamed = (Tameable) damager;
                    if ((tamed.isTamed())
                            && ((tamed.getOwner() instanceof Player))) {
                        return (Player) tamed.getOwner();
                    }
                }

            }
        }
        return null;
    }

    @Override
    public boolean canDragonNpcSpawn(String name) {
        if (this.damageTable.containsKey(name)) {
            return System.currentTimeMillis() - this.damageTable.get(name) <= maxDealTime;
        }
        return true;
    }
}
