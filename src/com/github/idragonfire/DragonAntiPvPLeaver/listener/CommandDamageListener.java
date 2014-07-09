package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.github.idragonfire.DragonAntiPvPLeaver.DAPL_Config;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DAttackerVictimEventListener;

public class CommandDamageListener implements DAttackerVictimEventListener,
        Listener {
    protected Logger logger;
    protected Hashtable<String, Long> cooldownTable;
    protected DAPL_Config config;
    protected boolean whitelistEnabled;
    protected String[] list;

    public CommandDamageListener(DAPL_Config config, Logger logger) {
        this.logger = logger;
        this.config = config;
        cooldownTable = new Hashtable<String, Long>();
        list = new String[0];

        initWhiteBlackList();
    }

    private void initWhiteBlackList() {
        if (config.pvp_blockcommands_whitelist_active
                && config.pvp_blockcommands_blacklist_active) {
            logger
                    .log(
                            Level.WARNING,
                            "blacklist.active AND whitelist.active was true !!! Plugin choosed whitelist mode.");
            config.pvp_blockcommands_blacklist_active = false;
        }
        if (config.pvp_blockcommands_whitelist_active) {
            whitelistEnabled = true;
            list = new String[config.pvp_blockcommands_whitelist_cmds.length];
            for (int i = 0; i < list.length; i++) {
                list[i] = "/" + config.pvp_blockcommands_whitelist_cmds[i];
            }
        } else {
            whitelistEnabled = false;
            list = new String[config.pvp_blockcommands_blacklist_cmds.length];
            for (int i = 0; i < list.length; i++) {
                list[i] = "/" + config.pvp_blockcommands_blacklist_cmds[i];
            }
        }
    }

    @Override
    public void onEntityDamageByEntity(LivingEntity attacker, Entity victim) {
        if (attacker instanceof HumanEntity) {
            cooldownTable.put(((HumanEntity) attacker).getName(), System
                    .currentTimeMillis()
                    + config.pvp_blockcommands_cooldown_ifhit * 1000);
        }
        if (victim instanceof HumanEntity) {
            cooldownTable.put(((HumanEntity) victim).getName(), System
                    .currentTimeMillis()
                    + config.pvp_blockcommands_cooldown_underattack * 1000);
        }
    }

    public boolean hasCooldown(String playername) {
        if (cooldownTable.containsKey(playername)) {
            return System.currentTimeMillis() < cooldownTable.get(playername);
        }
        return false;
    }

    public boolean canBypass(Player player) {
        return player.hasPermission(config.pvp_blockcommands_bypass);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerCommandPreprocessEvent(
            PlayerCommandPreprocessEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        if (canBypass(event.getPlayer())) {
            return;
        }
        if (hasCooldown(event.getPlayer().getName())) {
            if (whitelistEnabled) {
                for (int i = 0; i < list.length; i++) {
                    if (event.getMessage().startsWith(list[i])) {
                        return;
                    }
                }
            } else {
                boolean blacklisted = false;
                for (int i = 0; i < list.length; i++) {
                    if (event.getMessage().startsWith(list[i])) {
                        blacklisted = true;
                        break;
                    }
                }
                if (!blacklisted) {
                    return;
                }
            }
            event.setCancelled(true);
            event.getPlayer().sendMessage(
                    ChatColor.RED + config.language_commandBlocked);
        }
    }
}
