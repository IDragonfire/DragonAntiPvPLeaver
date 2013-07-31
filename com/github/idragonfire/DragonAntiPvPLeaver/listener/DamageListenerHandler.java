package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.ArrayList;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DAttackerVictimEventListener;

public class DamageListenerHandler {
    protected ArrayList<DAttackerVictimEventListener> listeners;

    public DamageListenerHandler() {
        listeners = new ArrayList<DAttackerVictimEventListener>();
    }

    public void addAttackVictionListener(DAttackerVictimEventListener listener) {
        listeners.add(listener);
    }

    public boolean hasRegisteredListeners() {
        return listeners.size() > 0;
    }

    public void onEntityDamageByEntity(EntityDamageEvent event) {
        LivingEntity attacker = getAttacker(event);
        if (attacker == null || event.getEntity() == null) {
            return;
        }
        for (DAttackerVictimEventListener listener : listeners) {
            listener.onEntityDamageByEntity(attacker, event.getEntity());
        }
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
