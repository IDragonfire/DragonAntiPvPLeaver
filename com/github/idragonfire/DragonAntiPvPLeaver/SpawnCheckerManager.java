package com.github.idragonfire.DragonAntiPvPLeaver;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnChecker;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnCheckerManager;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.WhitelistChecker;

public class SpawnCheckerManager implements DSpawnCheckerManager {

    protected ArrayList<WhitelistChecker> whitelist;
    protected ArrayList<DSpawnChecker> blacklist;
    protected DAPL_Config config;

    public SpawnCheckerManager(DAPL_Config config) {
        this.config = config;
        blacklist = new ArrayList<DSpawnChecker>();
        whitelist = new ArrayList<WhitelistChecker>();
    }

    public void addWhiteListChecker(WhitelistChecker checker) {
        whitelist.add(checker);
    }

    public void addBlacklistChecker(DSpawnChecker checker) {
        blacklist.add(checker);
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

        for (WhitelistChecker checker : whitelist) {
            if (checker.canNpcSpawn(player)) {
                return checker.getLifeTime(player);
            }
        }
        return NO_SPAWN;
    }

}
