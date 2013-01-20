package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.idragonfire.DragonAntiPvPLeaver.DAPL_Config;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DNpcManager;

public class DirtyListener extends DAntiPvPLeaverListener {

    public DirtyListener(DAPL_Config config, DNpcManager npcManager) {
        super(config, npcManager);
        // TODO Auto-generated constructor stub
    }

    @Override
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        super.onPlayerQuit(event);
    }

    @Override
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
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
            event.setCancelled(false);
            Player npc = (Player) event.getEntity();
            npcManager.npcAttackEvent(npc.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
