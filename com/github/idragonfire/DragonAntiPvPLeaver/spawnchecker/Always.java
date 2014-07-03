package com.github.idragonfire.DragonAntiPvPLeaver.spawnchecker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.DAPL_Config;

public class Always extends WhitelistChecker {

    public Always(DAPL_Config config) {
        super(config.npc_spawn_always_time);
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return true;
    }
}
