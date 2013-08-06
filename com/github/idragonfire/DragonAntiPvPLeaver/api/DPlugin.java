package com.github.idragonfire.DragonAntiPvPLeaver.api;

public interface DPlugin {
    public void addDaplPlayerListener(DPlayerListener listener);

    public void removeDaplPlayerListener(DPlayerListener listener);

    public DSpawnCheckerManager getSpawnChecker();

    public DDamagerListenerHandler getDamagerListenerHandler();
}
