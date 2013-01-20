package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnChecker;

public class DAlways implements DSpawnChecker {

    @Override
    public boolean canNpcSpawn(Player player) {
        return true;
    }

}
