package com.github.idragonfire.DragonAntiPvPLeaver.api;

import org.bukkit.entity.Player;

public interface DDisconnectionListener {
    public boolean onPlayerNmsDisconnect(Player player, Object playerConnection);
}
