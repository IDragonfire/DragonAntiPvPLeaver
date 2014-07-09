package com.github.idragonfire.DragonAntiPvPLeaver.deathfeatures;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DDeathFeature;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DPlayerListenerAdapter;

public class MoneyLose extends DPlayerListenerAdapter implements DDeathFeature {
    private Economy econ = null;
    private double moneyDelta;

    public MoneyLose(double moneyDelta) {
        this.moneyDelta = moneyDelta;
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
    }

    @Override
    public boolean validDeathListener() {
        return econ != null;
    }

    @Override
    public void playerNpcKilled(String name) {
        econ.withdrawPlayer(name, moneyDelta);
    }
}
