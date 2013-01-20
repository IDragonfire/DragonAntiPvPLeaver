package com.github.idragonfire.DragonAntiPvPLeaver.api;

import org.bukkit.entity.Player;

public interface DSpawnCheckerManager {
    public boolean canNpcSpawn(Player player);

    public boolean canBypass(Player player);
}
