package com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class DNearBy extends DWhitelistChecker {
    protected int distance;
    protected Class<? extends Entity> nearbyClass;

    public DNearBy(int distance, Class<? extends Entity> nearbyClass,
            int lifetime) {
        super(lifetime);
        this.distance = distance;
        this.nearbyClass = nearbyClass;
    }

    public boolean playersNearby(Player player) {
        for (Entity entity : player.getNearbyEntities(distance, distance,
                distance)) {
            if ((entity.getClass().isAssignableFrom(nearbyClass))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canNpcSpawn(Player player) {
        return playersNearby(player);
    }
}
