package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.listener.DDealDamage;

public class DIfHit extends DWhitelistChecker {
    protected DDealDamage listener;

    public DIfHit(DDealDamage listener, int lifetime) {
        super(lifetime);
        this.listener = listener;
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return listener.canDragonNpcSpawn(player.getName());
    }
}
