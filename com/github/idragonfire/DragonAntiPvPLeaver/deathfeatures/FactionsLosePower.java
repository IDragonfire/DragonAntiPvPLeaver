package com.github.idragonfire.DragonAntiPvPLeaver.deathfeatures;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DDeathFeature;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DPlayerListenerAdapter;
import com.massivecraft.factions.entity.UPlayer;

public class FactionsLosePower extends DPlayerListenerAdapter implements
        DDeathFeature {

    public boolean valid;
    public double powerDelta;

    public FactionsLosePower(double powerDelta) {
        this.powerDelta = powerDelta;
        Plugin factions = Bukkit.getPluginManager().getPlugin("Factions");
        valid = factions != null
                && factions.getDescription().getVersion().startsWith("2");
    }

    @Override
    public void playerNpcKilled(String name) {
        Player bukkitPlayer = Bukkit.getPlayerExact(name);
        if (bukkitPlayer == null) {
            return;
        }
        UPlayer player = UPlayer.get(name);
        if (player == null) {
            return;
        }
        double newPower = player.getPower() + powerDelta;
        if (-10 <= newPower && newPower <= 10) {
            player.setPower(newPower);
        }
    }

    @Override
    public boolean validDeathListener() {
        return valid;
    }

}
