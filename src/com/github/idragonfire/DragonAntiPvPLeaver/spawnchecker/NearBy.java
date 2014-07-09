package com.github.idragonfire.DragonAntiPvPLeaver.spawnchecker;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class NearBy extends WhitelistChecker {
    protected int distance;
    protected Class<? extends Entity> nearbyClass;

    public NearBy(int distance, Class<? extends Entity> nearbyClass,
            int lifetime) {
        super(lifetime);
        this.distance = distance;
        this.nearbyClass = nearbyClass;
    }

    public boolean playersNearby(Player player) {
        for (Entity entity : player.getNearbyEntities(distance, distance,
                distance)) {
            if ((nearbyClass.isAssignableFrom(entity.getClass()))) {
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
