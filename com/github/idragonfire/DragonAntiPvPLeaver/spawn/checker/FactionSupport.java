package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnChecker;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FFlag;

public class FactionSupport implements DSpawnChecker {
    protected boolean oldVersion_1_6;

    public FactionSupport() {
        Plugin factions = Bukkit.getPluginManager().getPlugin("Factions");
        oldVersion_1_6 = factions.getDescription().getVersion().startsWith(
                "1.6");
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canNpcSpawn(Player player) {
        // TODO: remove old Factions support
        if (oldVersion_1_6) {
            if (Board.getFactionAt(new FLocation(player.getLocation()))
                    .isSafeZone()) {
                return false;
            }
        } else {
            Faction playerFaction = Board.getFactionAt(new FLocation(player
                    .getLocation()));
            if (!playerFaction.getFlag(FFlag.PVP)
                    || playerFaction.getFlag(FFlag.PEACEFUL)) {
                return false;
            }
        }
        return true;
    }

}
