package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DWhitelistChecker;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.TakeDamageListener;

public class UnderAttack implements DWhitelistChecker {
    protected TakeDamageListener listener;

    public UnderAttack(TakeDamageListener listener) {
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
