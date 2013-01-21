package com.github.idragonfire.DragonAntiPvPLeaver;

public class DamageTrackerConfig {
    int lifetime;
    int cooldown;

    public DamageTrackerConfig(int lifetime, int cooldown) {
        super();
        this.lifetime = lifetime;
        this.cooldown = cooldown;
    }
}
