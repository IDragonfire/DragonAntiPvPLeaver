package com.github.idragonfire.DragonAntiPvPLeaver.api;

public interface DPlayerListener {
    public void playerNpcKilled(String name);

    public void playerNpcUnderAttack(String name);

    public void playerNpcSpawned(String name);

    public void playerNpcDespawned(String name);
}
