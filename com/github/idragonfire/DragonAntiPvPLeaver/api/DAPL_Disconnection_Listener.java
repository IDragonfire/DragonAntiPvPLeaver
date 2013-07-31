package com.github.idragonfire.DragonAntiPvPLeaver.api;

import org.bukkit.entity.Player;

public interface DAPL_Disconnection_Listener {
    public boolean onPlayerNmsDisconnect(Player player, Object playerConnection);
}
