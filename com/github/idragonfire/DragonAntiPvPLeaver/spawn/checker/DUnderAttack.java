package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.listener.DTakeDamage;

public class DUnderAttack extends DWhitelistChecker {
    protected DTakeDamage listener;

    public DUnderAttack(DTakeDamage listener, int lifetime) {
        super(lifetime);
        this.listener = listener;
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return listener.canDragonNpcSpawn(player.getName());
    }
}
