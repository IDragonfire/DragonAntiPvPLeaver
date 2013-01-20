package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnChecker;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DTakeDamage;

public class DUnderAttack implements DSpawnChecker {
    protected DTakeDamage listener;

    public DUnderAttack(DTakeDamage listener) {
        this.listener = listener;
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return listener.canDragonNpcSpawn(player.getName());
    }

}
