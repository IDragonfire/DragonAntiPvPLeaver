package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.listener.Listener_Normal;

public class IfHit extends WhitelistChecker {
    protected Listener_Normal listener;

    public IfHit(Listener_Normal listener, int lifetime) {
        super(lifetime);
        this.listener = listener;
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return listener.canDragonNpcSpawn(player.getName());
    }
}
