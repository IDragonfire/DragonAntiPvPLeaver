package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.HashMap;
import java.util.Hashtable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.DamageTrackerConfig;
import com.github.idragonfire.DragonAntiPvPLeaver.DAPL_Plugin.DAMAGE_MODE;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DAttackerVictimEventListener;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnCheckerManager;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DWhitelistChecker;

public abstract class DamageCooldownListenerTemplate implements
        DAttackerVictimEventListener, DWhitelistChecker {

    protected HashMap<DAMAGE_MODE, DamageTrackerConfig> mode;
    protected Hashtable<String, Long> cooldownTable;
    protected Hashtable<String, Integer> lifetimeTable;

    public DamageCooldownListenerTemplate(
            HashMap<DAMAGE_MODE, DamageTrackerConfig> mode) {
        this.mode = mode;
        cooldownTable = new Hashtable<String, Long>();
        lifetimeTable = new Hashtable<String, Integer>();
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return activeCooldown(player.getName());
    }

    public boolean activeCooldown(String name) {
        if (cooldownTable.containsKey(name)) {
            return System.currentTimeMillis() < cooldownTable.get(name);
        }
        return false;
    }

    @Override
    public int getLifeTime(Player player) {
        if (lifetimeTable.containsKey(player.getName())) {
            return lifetimeTable.get(player.getName());
        }
        return DSpawnCheckerManager.NO_SPAWN;
    }

    public void checkEntityType(Entity e, String playername) {
        if (mode.containsKey(DAMAGE_MODE.MONSTER) && e instanceof Monster) {
            cooldownTable.put(playername, System.currentTimeMillis()
                    + mode.get(DAMAGE_MODE.MONSTER).cooldown * 1000);
            lifetimeTable.put(playername,
                    mode.get(DAMAGE_MODE.MONSTER).lifetime);
            return;
        }
        if (mode.containsKey(DAMAGE_MODE.HUMANS) && e instanceof HumanEntity) {
            cooldownTable.put(playername, System.currentTimeMillis()
                    + mode.get(DAMAGE_MODE.HUMANS).cooldown * 1000);
            lifetimeTable
                    .put(playername, mode.get(DAMAGE_MODE.HUMANS).lifetime);
            return;
        }
    }

}
