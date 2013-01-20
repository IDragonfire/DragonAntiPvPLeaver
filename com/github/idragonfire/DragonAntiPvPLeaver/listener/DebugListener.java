package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredListener;

import com.github.idragonfire.DragonAntiPvPLeaver.DAPL_Config;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DNpcManager;

public class DebugListener extends DAntiPvPLeaverListener {
    protected Logger logger;

    public DebugListener(DAPL_Config config, DNpcManager npcManager,
            Logger logger) {
        super(config, npcManager);
        this.logger = logger;
    }

    @Override
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        super.onPlayerQuit(event);
    }

    @Override
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (npcManager.wasKilled(event.getPlayer().getName())) {
            logger.log(Level.WARNING, "NPC of " + event.getPlayer().getName()
                    + " died. Plugin try to kill him.");
            RegisteredListener[] listener = EntityDamageEvent.getHandlerList()
                    .getRegisteredListeners();
            for (int i = 0; i < listener.length; i++) {
                logger.log(Level.WARNING, listener[i].getListener().toString());
            }
        }

        super.onPlayerJoin(event);
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        super.onPlayerDeath(event);
    }

    @Override
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageEvent event) {
        try {
            if (!npcManager.isMyNpc(event.getEntity())) {
                return;
            }
            if (event.isCancelled()) {
                logger.log(Level.WARNING, "Some plugin cancel NPC damage:");
                RegisteredListener[] listener = EntityDamageEvent
                        .getHandlerList().getRegisteredListeners();
                for (int i = 0; i < listener.length; i++) {
                    logger.log(Level.WARNING, listener[i].getListener()
                            .toString());
                }
            }
            event.setCancelled(false);
            Player npc = (Player) event.getEntity();
            npcManager.npcAttackEvent(npc.getName());
            logger.log(Level.WARNING, "increase time");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
