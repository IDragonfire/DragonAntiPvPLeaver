package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnChecker;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DDealDamage;

public class DIfHit implements DSpawnChecker {
    protected DDealDamage listener;

    public DIfHit(DDealDamage listener) {
        this.listener = listener;
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return listener.canDragonNpcSpawn(player.getName());
    }

}
