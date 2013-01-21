package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DWhitelistChecker;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DealDamageListener;

public class IfHit implements DWhitelistChecker {
    protected DealDamageListener listener;

    public IfHit(DealDamageListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return listener.activeCooldown(player.getName());
    }

    @Override
    public int getLifeTime(Player player) {
        // TODO Auto-generated method stub
        return 0;
    }
}
