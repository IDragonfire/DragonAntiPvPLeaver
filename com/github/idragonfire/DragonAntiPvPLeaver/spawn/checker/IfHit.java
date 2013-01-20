package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.listener.DealDamageListener;

public class IfHit extends WhitelistChecker {
    protected DealDamageListener listener;

    public IfHit(DealDamageListener listener, int lifetime) {
        super(lifetime);
        this.listener = listener;
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return listener.mustDragonNpcSpawn(player.getName());
    }
}
