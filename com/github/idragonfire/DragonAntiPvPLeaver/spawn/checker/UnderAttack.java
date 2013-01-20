package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.listener.TakeDamageListener;

public class UnderAttack extends WhitelistChecker {
    protected TakeDamageListener listener;

    public UnderAttack(TakeDamageListener listener, int lifetime) {
        super(lifetime);
        this.listener = listener;
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return listener.mustDragonNpcSpawn(player.getName());
    }
}
