package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnChecker;

public abstract class WhitelistChecker implements DSpawnChecker {

    protected int lifetime;

    public WhitelistChecker(int lifetime) {
        this.lifetime = lifetime;
    }

    public abstract boolean canNpcSpawn(Player player);

    public int getLifeTime(Player player) {
        return lifetime;
    }

}
