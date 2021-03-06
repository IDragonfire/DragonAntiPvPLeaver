package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listener_Debug extends Listener_Normal {
	protected Logger logger;

	public Listener_Debug(Logger logger) {
		this.logger = logger;
	}

	@Override
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		logger.log(Level.INFO, "onPlayerQuit");
		super.onPlayerQuit(event);
	}

	@Override
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		logger.log(Level.INFO, "onPlayerJoin");
		super.onPlayerJoin(event);
	}

	@Override
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		logger.log(Level.INFO, "onPlayerDeath");
		super.onPlayerDeath(event);
	}

	@Override
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageEvent event) {
		logger.log(Level.INFO, "onEntityDamageByEntity");
		super.onEntityDamageByEntity(event);
	}
}
