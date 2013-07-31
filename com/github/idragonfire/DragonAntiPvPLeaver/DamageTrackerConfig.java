package com.github.idragonfire.DragonAntiPvPLeaver;

public class DamageTrackerConfig {
    public int lifetime;
    public int cooldown;

    public DamageTrackerConfig(int lifetime, int cooldown) {
        super();
        this.lifetime = lifetime;
        this.cooldown = cooldown;
    }
}
