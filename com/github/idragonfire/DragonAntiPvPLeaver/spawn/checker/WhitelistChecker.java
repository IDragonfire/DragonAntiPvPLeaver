package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DWhitelistChecker;

public abstract class WhitelistChecker implements DWhitelistChecker {

    protected int lifetime;

    public WhitelistChecker(int lifetime) {
        this.lifetime = lifetime;
    }

    public int getLifeTime(Player player) {
        return lifetime;
    }

}
