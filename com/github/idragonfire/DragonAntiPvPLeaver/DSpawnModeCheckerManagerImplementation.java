package com.github.idragonfire.DragonAntiPvPLeaver;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnChecker;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnCheckerManager;

public class DSpawnModeCheckerManagerImplementation implements
        DSpawnCheckerManager {

    protected ArrayList<DSpawnChecker> checkers;
    protected DAPL_Config config;

    public DSpawnModeCheckerManagerImplementation(DAPL_Config config) {
        this.config = config;
        checkers = new ArrayList<DSpawnChecker>();
    }

    @Override
    public boolean canBypass(Player player) {
        return player.hasPermission(config.npc_spawn_bypass);
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        if (canBypass(player) || (player.getGameMode().getValue() == 1)) {
            return false;
        }

        for (DSpawnChecker checker : checkers) {
            if (!checker.canNpcSpawn(player)) {
                return false;
            }
        }
        return true;
    }

}
