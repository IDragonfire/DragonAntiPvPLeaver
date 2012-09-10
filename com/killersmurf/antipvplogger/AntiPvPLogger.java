package com.killersmurf.antipvplogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.killersmurf.antipvplogger.listeners.AntiPvPLoggerListener;
import com.topcat.npclib.NPCManager;
import com.topcat.npclib.entity.HumanNPC;
import com.topcat.npclib.entity.NPC;

public class AntiPvPLogger extends JavaPlugin {
    public static final Logger LOGGER = Logger.getLogger("Minecraft");
    private List<String> deadPlayers;
    private Map<String, DeSpawnTask> taskMap;
    private int time;
    private int distance;
    private YamlConfiguration dataFile;
    private NPCManager npcManager;

    @Override
    public void onEnable() {
        this.deadPlayers = new ArrayList<String>();
        this.taskMap = new HashMap<String, DeSpawnTask>();
        loadConfig();
        loadDataFile();
        loadDeadPlayers();
        Bukkit.getPluginManager().registerEvents(
                new AntiPvPLoggerListener(this), this);
        this.npcManager = new NPCManager(this);
        loadDataFile();
    }

    @Override
    public void onDisable() {
        saveConfig();
        saveDataFile();
        saveDeadPlayers();
    }

    public boolean playersNearby(Player player) {
        for (Entity entity : player.getNearbyEntities(this.distance,
                this.distance, this.distance)) {
            if ((entity instanceof Player)) {
                return true;
            }
        }
        return false;
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
        this.taskMap.get(name).increaseTime(this.time * 20L);
    }

    public void saveDeadPlayers() {
        AntiPvPLogger.log(Level.INFO, "Saving " + this.deadPlayers.size()
                + " Dead Players.");
        getDataFile().set("deadPlayers", this.deadPlayers);
        saveDataFile();
        AntiPvPLogger.log(Level.INFO, "[AntiPvPLogger] Saving Complete.");
    }

    public void loadDeadPlayers() {
        if (getDataFile().getList("deadPlayers") == null) {
            LOGGER.log(Level.INFO,
                    "[AntiPvPLogger] Could not load any Dead Players.");
            return;
        }
        // TODO: load
        this.deadPlayers = getDataFile().getStringList("deadPlayers");
        getDataFile().set("deadPlayers", null);
        saveDataFile();
        LOGGER.log(Level.INFO, "[AntiPvPLogger] Loaded "
                + this.deadPlayers.size() + " Dead Players.");
    }

    public YamlConfiguration loadDataFile() {
        File df = new File(getDataFolder().toString() + File.separator
                + "data.yml");

        if (!df.exists()) {
            try {
                df.createNewFile();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Could not create the data file!", ex);
            }
        }

        this.dataFile = YamlConfiguration.loadConfiguration(df);
        return this.dataFile;
    }

    public YamlConfiguration getDataFile() {
        return this.dataFile;
    }

    public void saveDataFile() {
        File df = new File(getDataFolder().toString() + File.separator
                + "data.yml");
        try {
            this.dataFile.save(df);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not save the data!", ex);
        }
    }

    public void loadConfig() {
        Configuration config = getConfig();

        config.addDefault("npc.spawn.distance", Integer.valueOf(10));
        // config.set("npc.spawn.distance", Integer.valueOf(config
        // .getInt("npc.spawn.distance")));
        this.distance = Integer.valueOf(config.getInt("npc.spawn.distance"));

        config.addDefault("npc.spawn.time", Integer.valueOf(15));
        // config.set("npc.spawn.time", Integer.valueOf(config
        // .getInt("npc.spawn.time")));
        this.time = Integer.valueOf(config.getInt("npc.spawn.time"));

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[AntiPvPLogger] " + message);
    }

}