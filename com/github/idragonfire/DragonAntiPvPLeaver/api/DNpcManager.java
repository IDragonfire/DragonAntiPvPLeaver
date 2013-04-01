package com.github.idragonfire.DragonAntiPvPLeaver.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface DNpcManager {
    public void spawnHumanNPC(Player player, int lifetime, Object playerConnection);

    public void despawnHumanByName(String name);

    public void addKillStatus(String name);

    public boolean wasKilled(String name);

    public void removeKilledStatus(String name);

    public boolean isMyNpc(Entity entity);

    public void npcAttackEvent(String name);

}
