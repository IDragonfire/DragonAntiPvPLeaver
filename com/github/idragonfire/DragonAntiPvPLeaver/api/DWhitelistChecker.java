package com.github.idragonfire.DragonAntiPvPLeaver.api;

import org.bukkit.entity.Player;

public interface DWhitelistChecker extends DSpawnChecker {

    public abstract int getLifeTime(Player player);

}