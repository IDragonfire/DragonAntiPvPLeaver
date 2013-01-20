package com.github.idragonfire.DragonAntiPvPLeaver;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnChecker;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnCheckerManager;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.DWhitelistChecker;

public class DSpawnModeCheckerManagerImplementation implements
        DSpawnCheckerManager {

    protected ArrayList<DWhitelistChecker> whitelist;
    protected ArrayList<DSpawnChecker> blacklist;
    protected DAPL_Config config;

    public DSpawnModeCheckerManagerImplementation(DAPL_Config config) {
        this.config = config;
        blacklist = new ArrayList<DSpawnChecker>();
        whitelist = new ArrayList<DWhitelistChecker>();
    }

    @Override
    public boolean canBypass(Player player) {
        return player.hasPermission(config.npc_spawn_bypass);
    }

    @Override
    public int dragonNpcSpawnTime(Player player) {
        if (canBypass(player) || (player.getGameMode().getValue() == 1)) {
            return NO_SPAWN;
        }

        for (DSpawnChecker checker : blacklist) {
            if (!checker.canNpcSpawn(player)) {
                return NO_SPAWN;
            }
        }

        for (DWhitelistChecker checker : whitelist) {
            if (checker.canNpcSpawn(player)) {
                return checker.getLifeTime(player);
            }
        }
        return NO_SPAWN;
    }

}
