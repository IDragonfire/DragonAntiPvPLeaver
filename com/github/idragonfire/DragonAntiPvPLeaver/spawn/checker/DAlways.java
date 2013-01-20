package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.DAPL_Config;

public class DAlways extends DWhitelistChecker {

    public DAlways(DAPL_Config config) {
        super(config.npc_spawn_always_time);
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return true;
    }
}
