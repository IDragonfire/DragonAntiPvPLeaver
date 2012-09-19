package com.github.idragonfire.DragonAntiPvPLeaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.idragonfire.DragonAntiPvPLeaver.listener.DAntiPvPLeaverListener;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DebugListener;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DirtyListener;
import com.github.idragonfire.DragonAntiPvPLeaver.metrics.Metrics;
import com.topcat.npclib.DragonAntiPvPListener.NPCManager;
import com.topcat.npclib.DragonAntiPvPListener.entity.HumanNPC;
import com.topcat.npclib.DragonAntiPvPListener.entity.NPC;

public class DAntiPvPLeaverPlugin extends JavaPlugin {
    protected List<String> deadPlayers;
    protected Map<String, DeSpawnTask> taskMap;
    protected YamlConfiguration dataFile;
    protected NPCManager npcManager;
    protected HashMap<String, String> lang;

    protected boolean debugMode;
    protected boolean overwriteAllNpcDamageListener;
    protected boolean spawnOnlyIfPlayerNearby;
    protected int distance;
    protected int time;
    protected int additionalTimeIfUnderAttack;
    protected int broadcastMessageRadius;

    @Override
    public void onEnable() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.deadPlayers = new ArrayList<String>();
        this.taskMap = new HashMap<String, DeSpawnTask>();
        this.lang = new HashMap<String, String>();
        loadConfig();
        loadDeadPlayers();
        if (this.debugMode) {
            Bukkit.getPluginManager().registerEvents(new DebugListener(this),
                    this);
        } else if (this.overwriteAllNpcDamageListener) {
            Bukkit.getPluginManager().registerEvents(new DirtyListener(this),
                    this);
        } else {
            Bukkit.getPluginManager().registerEvents(
                    new DAntiPvPLeaverListener(this), this);
        }
        this.npcManager = new NPCManager(this);
    }

    @Override
    public void onDisable() {
        reloadConfig();
        saveConfig();
        saveDeadPlayers();
    }

    @Override
    public void saveConfig() {
        getConfig().options().copyDefaults(true);
        super.saveConfig();
    }

    public void loadConfig() {
        Configuration config = getConfig();
        this.debugMode = config.getBoolean("plugin.debug");
        this.overwriteAllNpcDamageListener = config
                .getBoolean("plugin.overwriteAllNpcDamageListener");
        this.spawnOnlyIfPlayerNearby = config
                .getBoolean("npc.spawn.onlyIfPlayerNearby");
        this.distance = config.getInt("npc.spawn.distance");
        this.time = config.getInt("npc.spawn.time");

        this.additionalTimeIfUnderAttack = config
                .getInt("npc.spawn.additionalTimeIfUnderAttack");
        this.broadcastMessageRadius = config
                .getInt("npc.spawn.broadcastMessageRadius");
        saveConfig();
    }

    public void saveDeadPlayers() {
        getLogger().log(Level.INFO,
                "Saving " + this.deadPlayers.size() + " Dead Players.");
        getDataFile().set("deadPlayers", this.deadPlayers);
        saveDataFile();
        getLogger().log(Level.INFO, "Saving Complete.");
    }

    public void saveDataFile() {
        File f = new File(getDataFolder().toString() + File.separator
                + "data.yml");
        try {
            this.dataFile.save(f);
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
        this.deadPlayers = getDataFile().getStringList("deadPlayers");
        getDataFile().set("deadPlayers", null);
        saveDataFile();
        getLogger().log(Level.INFO,
                "Loaded " + this.deadPlayers.size() + " Dead Players.");
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
        this.dataFile = YamlConfiguration.loadConfiguration(df);
        return this.dataFile;
    }

    public String getLang(String key) {
        String text = this.lang.get(key);
        if (text != null) {
            return text;
        }
        text = getConfig().getString("language." + key);
        this.lang.put(key, text);
        return text;
    }

    public boolean playersNearby(Player player) {
        if (!this.spawnOnlyIfPlayerNearby) {
            return true;
        }
        for (Entity entity : player.getNearbyEntities(this.distance,
                this.distance, this.distance)) {
            if ((entity instanceof Player)) {
                return true;
            }
        }
        return false;
    }

    public NPC getOneHumanNPCByName(String name) {
        try {
            return this.npcManager.getHumanNPCByName(name).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HumanNPC spawnHumanNPC(Player player, Location loc, String name) {
        HumanNPC npc = (HumanNPC) this.npcManager.spawnHumanNPC(name, loc);
        ItemStack[] invContents = player.getInventory().getContents();
        ItemStack[] armourContents = player.getInventory().getArmorContents();
        npc.getInventory().setContents(invContents);
        npc.getInventory().setArmorContents(armourContents);
        DeSpawnTask task = new DeSpawnTask(name, this.npcManager, this);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, task,
                this.time * 20L);
        this.taskMap.put(npc.getName(), task);
        return npc;
    }

    public void npcFirstTimeAttacked(String name) {
        this.taskMap.get(name).increaseTime(
                this.additionalTimeIfUnderAttack * 20L);
    }

    public void broadcastNearPlayer(Player playerForRadiusBroadcast,
            String message) {
        List<Player> players = playerForRadiusBroadcast.getWorld().getPlayers();
        Location loc = playerForRadiusBroadcast.getLocation();
        for (Player player : players) {
            if (player.getLocation().distance(loc) < this.broadcastMessageRadius) {
                player.sendMessage(message);
            }
        }
    }

    public void addDead(String name) {
        this.deadPlayers.add(name);
    }

    public void removeDead(String name) {
        this.deadPlayers.remove(name);
    }

    public boolean isDead(String name) {
        return this.deadPlayers.contains(name);
    }

    public boolean isAntiPvpNPC(Entity entity) {
        return this.npcManager.isNPC(entity);
    }

    public void despawnHumanByName(String npcName) {
        this.npcManager.despawnHumanByName(npcName);
    }

    public YamlConfiguration getDataFile() {
        return this.dataFile;
    }
}