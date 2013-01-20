package com.github.idragonfire.DragonAntiPvPLeaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.h31ix.updater.DragonAntiPvpLeaver.Updater;
import net.h31ix.updater.DragonAntiPvpLeaver.Updater.UpdateResult;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.idragonfire.DragonAntiPvPLeaver.listener.DAntiPvPLeaverListener;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DDealDamage;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DTakeDamage;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DebugListener;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DirtyListener;
import com.github.idragonfire.DragonAntiPvPLeaver.metrics.Metrics;
import com.github.idragonfire.DragonAntiPvPLeaver.metrics.Metrics.Graph;
import com.github.idragonfire.DragonAntiPvPLeaver.metrics.Metrics.Plotter;

import de.kumpelblase2.remoteentities.RemoteEntities;
import de.kumpelblase2.remoteentities.exceptions.PluginNotEnabledException;

public class DAntiPvPLeaverPlugin extends JavaPlugin implements Listener {
    protected List<String> deadPlayers;
    protected Map<String, DeSpawnTask> taskMap;
    protected YamlConfiguration dataFile;
    protected DAPL_NPCManager npcManager;
    public DAPL_Config config;

    public enum DAMAGE_MODE {
        CREATURE, HUMANS
    }

    public static long checkEntityType(Entity e,
            HashMap<DAMAGE_MODE, Integer> mode) {
        if (mode.containsKey(DAMAGE_MODE.CREATURE) && e instanceof Creature) {
            return System.currentTimeMillis() + mode.get(DAMAGE_MODE.CREATURE);
        }
        if (mode.containsKey(DAMAGE_MODE.HUMANS) && e instanceof HumanEntity) {
            return System.currentTimeMillis() + mode.get(DAMAGE_MODE.HUMANS);
        }
        return -1;
    }

    @Override
    public void onEnable() {
        try {
            npcManager = new DAPL_NPCManager(
                    RemoteEntities.createManager(this), this);
        } catch (PluginNotEnabledException e) {
            e.printStackTrace();
            onDisable();
            return;
        }

        deadPlayers = new ArrayList<String>();
        taskMap = new HashMap<String, DeSpawnTask>();
        loadConfig();
        loadDeadPlayers();

        // set listener mode
        String listenerMode = "normal";
        DAntiPvPLeaverListener listener = null;
        if (getConfig().getBoolean("plugin.debug")) {
            listener = new DebugListener(this);
            listenerMode = "debug";
        } else if (getConfig().getBoolean(
                "plugin.overwriteAllNpcDamageListener")) {
            listener = new DirtyListener(this);
            listenerMode = "overwrite";
        } else {
            listener = new DAntiPvPLeaverListener(this);
        }

        initSpawnModes(listener);
        Bukkit.getPluginManager().registerEvents(listener, this);

        enableMetrics(listenerMode);
        enableAutoUpdate();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void initSpawnModes(DAntiPvPLeaverListener listener) {
        // Deal Damage Listener
        HashMap<DAMAGE_MODE, Integer> dealerConfig = new HashMap<DAMAGE_MODE, Integer>();
        if (config.npc_spawn_ifhitmonster_active) {
            dealerConfig.put(DAMAGE_MODE.CREATURE,
                    config.npc_spawn_ifhitplayer_time);
        }
        if (config.npc_spawn_ifhitplayer_active) {
            dealerConfig.put(DAMAGE_MODE.HUMANS,
                    config.npc_spawn_ifhitplayer_time);
        }
        listener.addListener(new DDealDamage(dealerConfig));

        // Take Damage Listener
        HashMap<DAMAGE_MODE, Integer> takerConfig = new HashMap<DAMAGE_MODE, Integer>();
        if (config.npc_spawn_underattackfromMonsters_active) {
            takerConfig.put(DAMAGE_MODE.CREATURE,
                    config.npc_spawn_underattackfromMonsters_time);
        }
        if (config.npc_spawn_underattackfromplayers_active) {
            takerConfig.put(DAMAGE_MODE.HUMANS,
                    config.npc_spawn_underattackfromplayers_time);
        }
        listener.addListener(new DTakeDamage(takerConfig));
    }

    protected void enableMetrics(String listenerMode) {
        try {
            Metrics metrics = new Metrics(this);

            if (getConfig().getBoolean("metrics.listenerMode")) {
                // custom graph #1 - Listener Mode
                final Graph listenerGraph = metrics
                        .createGraph("Listener Mode");
                listenerGraph.addPlotter(new SimplePlotter(listenerMode));
            }

            if (getConfig().getBoolean("metrics.listenerMode")) {
                // custom graph #2 - WorldGuard Usage
                final Graph worldGuardGraph = metrics
                        .createGraph("WorldGuard Usage");
                if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
                    worldGuardGraph.addPlotter(new SimplePlotter(Bukkit
                            .getPluginManager().getPlugin("WorldGuard")
                            .getDescription().getVersion()));
                } else {
                    worldGuardGraph.addPlotter(new SimplePlotter("no"));
                }
            }

            if (getConfig().getBoolean("metrics.factionsUsage")) {
                // custom graph #3 - Factions Usage
                final Graph factionsGraph = metrics
                        .createGraph("Factions Usage");
                if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
                    factionsGraph.addPlotter(new SimplePlotter(Bukkit
                            .getPluginManager().getPlugin("Factions")
                            .getDescription().getVersion()));
                } else {
                    factionsGraph.addPlotter(new SimplePlotter("no"));
                }
            }

            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void enableAutoUpdate() {
        try {
            String updateMode = getConfig().getString("plugin.autoupdate");
            if (updateMode.equals("off") || updateMode.equals("false")) {
                return;
            }
            Updater.UpdateType updateType = Updater.UpdateType.NO_DOWNLOAD;
            if (updateMode.equals("automaticDownload")) {
                updateType = Updater.UpdateType.DEFAULT;
            }
            Updater updater = new Updater(this, "dragonantipvpleaver",
                    getFile(), updateType, false);
            UpdateResult result = updater.getResult();
            switch (result) {
            case UPDATE_AVAILABLE:
                getLogger().log(Level.INFO, "#########################");
                getLogger().log(Level.INFO, "New version available: ");
                getLogger().log(Level.INFO, updater.getLatestVersionString());
                getLogger().log(Level.INFO,
                        "Your version: " + getDescription().getVersion());
                getLogger().log(Level.INFO, "#########################");
                break;
            case SUCCESS:
                getLogger().log(
                        Level.INFO,
                        "downloaded successfull "
                                + updater.getLatestVersionString()
                                + ". Updating plugin at next server restart!");
                break;
            case NO_UPDATE:
                break;

            default:
                getLogger().log(Level.WARNING, " Updater has problems");
                break;
            }
            if (result == UpdateResult.UPDATE_AVAILABLE) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
        saveDeadPlayers();
    }

    @Override
    public void saveConfig() {
        config.save();
    }

    public void loadConfig() {
        config = new DAPL_Config(this);
        config.load();
        config.save();
    }

    public void saveDeadPlayers() {
        getLogger().log(Level.INFO,
                "Saving " + deadPlayers.size() + " Dead Players.");
        getDataFile().set("deadPlayers", deadPlayers);
        saveDataFile();
        getLogger().log(Level.INFO, "Saving Complete.");
    }

    public void saveDataFile() {
        File f = new File(getDataFolder().toString() + File.separator
                + "data.yml");
        try {
            dataFile.save(f);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save the data!");
            e.printStackTrace();
        }
    }

    public void loadDeadPlayers() {
        loadDataFile();
        if (getDataFile().getList("deadPlayers") == null) {
            getLogger().log(Level.INFO, "Could not load any Dead Players.");
            return;
        }
        deadPlayers = getDataFile().getStringList("deadPlayers");
        getDataFile().set("deadPlayers", null);
        saveDataFile();
        getLogger().log(Level.INFO,
                "Loaded " + deadPlayers.size() + " Dead Players.");
    }

    public YamlConfiguration loadDataFile() {
        File df = new File(getDataFolder().toString() + File.separator
                + "data.yml");
        if (!df.exists()) {
            try {
                df.createNewFile();
            } catch (IOException e) {
                getLogger()
                        .log(Level.SEVERE, "Could not create the data file!");
                e.printStackTrace();
            }
        }
        dataFile = YamlConfiguration.loadConfiguration(df);
        return dataFile;
    }

    /** NPC stuff # START **/

    public boolean isAntiPvpNPC(Entity entity) {
        return npcManager.isDragonNPC(entity);
    }

    public void despawnHumanByName(String npcID) {
        npcManager.despawnPlayerNPC(npcID);
    }

    public void spawnHumanNPC(Player player, int timeInSeconds) {
        // TODO: use different time for each case
        String npcID = npcManager.spawnPlayerNPC(player);
        DeSpawnTask task = new DeSpawnTask(npcID, npcManager, this);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, task,
                timeInSeconds * 20L);
        taskMap.put(npcID, task);
    }

    /** NPC stuff # END **/

    public void npcFirstTimeAttacked(String name) {
        System.out.println("increase time");
        System.out.println(name);
        taskMap.get(name).increaseTime(
                config.npc_additionalTimeIfUnderAttack * 20L);
    }

    public void broadcastNearPlayer(Player playerForRadiusBroadcast,
            String message) {
        List<Player> players = playerForRadiusBroadcast.getWorld().getPlayers();
        Location loc = playerForRadiusBroadcast.getLocation();
        for (Player player : players) {
            if (player.getLocation().distance(loc) < config.npc_broadcastMessageRadius) {
                player.sendMessage(message);
            }
        }
    }

    public void addDead(String name) {
        deadPlayers.add(name);
    }

    public void removeDead(String name) {
        deadPlayers.remove(name);
    }

    public boolean isDead(String name) {
        return deadPlayers.contains(name);
    }

    public YamlConfiguration getDataFile() {
        return dataFile;
    }

    public boolean printMessages() {
        return config.plugin_printMessages;
    }

    private class SimplePlotter extends Plotter {
        public SimplePlotter(final String name) {
            super(name);
        }

        @Override
        public int getValue() {
            return 1;
        }
    }
}