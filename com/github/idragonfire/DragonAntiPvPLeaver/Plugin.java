package com.github.idragonfire.DragonAntiPvPLeaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.idragonfire.DragonAntiPvPLeaver.listener.DealDamageListener;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.Listener_Debug;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.Listener_Dirty;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.Listener_Normal;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.TakeDamageListener;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.Always;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.FactionSupport;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.IfHit;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.NearBy;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.UnderAttack;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.WorldGuardSupport;
import com.github.idragonfire.DragonAntiPvPLeaver.util.Metrics;
import com.github.idragonfire.DragonAntiPvPLeaver.util.Updater;
import com.github.idragonfire.DragonAntiPvPLeaver.util.Metrics.Graph;
import com.github.idragonfire.DragonAntiPvPLeaver.util.Metrics.Plotter;
import com.github.idragonfire.DragonAntiPvPLeaver.util.Updater.UpdateResult;

import de.kumpelblase2.remoteentities.RemoteEntities;
import de.kumpelblase2.remoteentities.exceptions.PluginNotEnabledException;

public class Plugin extends JavaPlugin implements Listener {
    protected List<String> deadPlayers;
    protected YamlConfiguration dataFile;
    protected DAPL_NpcManager npcManager;
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
            npcManager = new DAPL_NpcManager(
                    RemoteEntities.createManager(this), this);
        } catch (PluginNotEnabledException e) {
            e.printStackTrace();
            onDisable();
            return;
        }

        deadPlayers = new ArrayList<String>();
        loadConfig();
        loadDeadPlayers();

        // set listener mode
        String listenerMode = "normal";
        Listener_Normal listener = null;
        if (getConfig().getBoolean("plugin.debug")) {
            listener = new Listener_Debug(getLogger());
            listenerMode = "debug";
        } else if (getConfig().getBoolean(
                "plugin.overwriteAllNpcDamageListener")) {
            listener = new Listener_Dirty();
            listenerMode = "overwrite";
        } else {
            listener = new Listener_Normal();
        }

        listener.init(config, npcManager);
        initSpawnModes(listener);
        Bukkit.getPluginManager().registerEvents(listener, this);

        enableMetrics(listenerMode);
        enableAutoUpdate();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void initSpawnModes(Listener_Normal listener) {
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
        DealDamageListener dealDamageListener = new DealDamageListener(
                dealerConfig);
        listener.addListener(dealDamageListener);

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
        TakeDamageListener takeDamageListener = new TakeDamageListener(
                takerConfig);
        listener.addListener(takeDamageListener);

        // init spawn modes
        SpawnCheckerManager manager = new SpawnCheckerManager(config);
        if (config.npc_spawn_always_active) {
            manager.addWhiteListChecker(new Always(config));
            getLogger().log(Level.INFO, "spawn mode: always");
        } else {
            if (config.npc_spawn_playernearby_active) {
                manager.addWhiteListChecker(new NearBy(
                        config.npc_spawn_playernearby_distance,
                        HumanEntity.class, config.npc_spawn_playernearby_time));
            }
            if (config.npc_spawn_monsternearby_active) {
                manager.addWhiteListChecker(new NearBy(
                        config.npc_spawn_monsternearby_distance, Monster.class,
                        config.npc_spawn_monsternearby_time));
            }
            // TODO: add lifetime to config and rename time to timeuntilreset
            if (config.npc_spawn_underattackfromMonsters_active
                    || config.npc_spawn_underattackfromplayers_active) {
                manager.addWhiteListChecker(new UnderAttack(takeDamageListener,
                        20));
            }
            // TODO: add lifetime to config and rename time to timeuntilreset
            if (config.npc_spawn_ifhitmonster_active
                    || config.npc_spawn_ifhitplayer_active) {
                manager.addWhiteListChecker(new IfHit(dealDamageListener, 20));
            }
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
            manager.addBlacklistChecker(new FactionSupport());
            getLogger().log(Level.INFO, "Factions support enabled.");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            manager.addBlacklistChecker(new WorldGuardSupport());
            getLogger().log(Level.INFO, "WorldGuard support enabled.");
        }
    }

    protected void enableMetrics(String listenerMode) {
        try {
            Metrics metrics = new Metrics(this);

            if (config.metrics_listenerMode) {
                // custom graph #1 - Listener Mode
                final Graph listenerGraph = metrics
                        .createGraph("Listener Mode");
                listenerGraph.addPlotter(new SimplePlotter(listenerMode));
            }

            if (config.metrics_worldGuardUsage) {
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

            if (config.metrics_factionsUsage) {
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
            String updateMode = config.plugin_autoupdate;
            if (updateMode.equals(config.plugin_update_none)
                    || updateMode.equals("false")) {
                return;
            }
            Updater.UpdateType updateType = Updater.UpdateType.NO_DOWNLOAD;
            if (updateMode.equals(config.plugin_update_automatic)) {
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
                "Saving " + deadPlayers.size() + " dead players.");
        dataFile.set("deadPlayers", deadPlayers);
        saveDataFile();
        getLogger().log(Level.INFO, "Saving dead players complete.");
    }

    public void saveDataFile() {
        File f = new File(getDataFolder().toString() + File.separator
                + "data.yml");
        try {
            dataFile.save(f);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save the dead players!");
            e.printStackTrace();
        }
    }

    public void loadDeadPlayers() {
        loadDataFile();
        if (dataFile.getList("deadPlayers") == null) {
            getLogger().log(Level.INFO, "Could not load any dead player.");
            return;
        }
        deadPlayers = dataFile.getStringList("deadPlayers");
        dataFile.set("deadPlayers", null);
        saveDataFile();
        getLogger().log(Level.INFO,
                "Loaded " + deadPlayers.size() + " dead players.");
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

    public static void broadcastNearPlayer(Player playerForRadiusBroadcast,
            String message, int radius) {
        List<Player> players = playerForRadiusBroadcast.getWorld().getPlayers();
        Location loc = playerForRadiusBroadcast.getLocation();
        for (Player player : players) {
            if (player.getLocation().distance(loc) < radius) {
                player.sendMessage(message);
            }
        }
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