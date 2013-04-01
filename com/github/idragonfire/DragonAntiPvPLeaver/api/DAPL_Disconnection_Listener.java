package com.github.idragonfire.DragonAntiPvPLeaver.api;

import org.bukkit.entity.Player;

import com.github.idragonfire.DragonAntiPvPLeaver.DAPL_Config;

public interface DAPL_Disconnection_Listener {
    public boolean onPlayerNmsDisconnect(Player player);
}
