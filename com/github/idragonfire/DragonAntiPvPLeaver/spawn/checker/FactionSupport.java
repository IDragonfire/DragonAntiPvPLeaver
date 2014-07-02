package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnChecker;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.mcore.ps.PS;

public class FactionSupport implements DSpawnChecker {
	protected boolean version_2_x;
	protected boolean oldVersion_1_6;

	public FactionSupport() {
		Plugin factions = Bukkit.getPluginManager().getPlugin("Factions");
		String version = factions.getDescription().getVersion();
		version_2_x = version.startsWith("2");
		oldVersion_1_6 = version.startsWith("1.6");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean canNpcSpawn(Player player) {
		// TODO: version warning
		if (version_2_x) {
			com.massivecraft.factions.entity.Faction faction = BoardColls.get()
					.getFactionAt(PS.valueOf(player.getLocation()));
			if (!faction.getFlag(com.massivecraft.factions.FFlag.PVP)
					|| faction
							.getFlag(com.massivecraft.factions.FFlag.PEACEFUL)) {
				return false;
			}
		}
		return true;
	}
}
