package com.github.idragonfire.DragonAntiPvPLeaver.api;

import org.bukkit.entity.Player;

public interface DSpawnCheckerManager {
    public final int NO_SPAWN = -1;

    public int dragonNpcSpawnTime(Player player);

    public boolean canBypass(Player player);
}
