package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnChecker;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class WorldGuardSupport implements DSpawnChecker {
    protected WorldGuardPlugin worldGuard;

    public WorldGuardSupport() {
        worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin(
                "WorldGuard");
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
        RegionManager regionManager = worldGuard.getRegionManager(player
                .getWorld());
        ApplicableRegionSet set = regionManager.getApplicableRegions(player
                .getLocation());
        return set.allows(DefaultFlag.PVP, localPlayer);
    }
}
