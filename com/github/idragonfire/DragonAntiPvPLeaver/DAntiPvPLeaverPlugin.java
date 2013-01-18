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
import com.github.idragonfire.DragonAntiPvPLeaver.metrics.Metrics.Graph;
import com.github.idragonfire.DragonAntiPvPLeaver.metrics.Metrics.Plotter;
import com.topcat.npclib.DragonAntiPvPListener.NPCManager;
import com.topcat.npclib.DragonAntiPvPListener.entity.HumanNPC;
import com.topcat.npclib.DragonAntiPvPListener.entity.NPC;

public class DAntiPvPLeaverPlugin extends JavaPlugin {
    protected List<String> deadPlayers;
    protected Map<String, DeSpawnTask> taskMap;
    protected YamlConfiguration dataFile;
    protected NPCManager npcManager;
    protected HashMap<String, String> lang;

    protected boolean spawnOnlyIfPlayerNearby;
    protected int distance;
    protected int time;
    protected int additionalTimeIfUnderAttack;
    protected int broadcastMessageRadius;
    protected boolean printMessages;
    protected boolean vanillaExpDrop;
    protected String npcTagNameColor;

    @Override
    public void onEnable() {
        this.deadPlayers = new ArrayList<String>();
        this.taskMap = new HashMap<String, DeSpawnTask>();
        this.lang = new HashMap<String, String>();
        loadConfig();
        loadDeadPlayers();

        String listenerMode = "normal";
        if (getConfig().getBoolean("plugin.debug")) {
            Bukkit.getPluginManager().registerEvents(new DebugListener(this),
                    this);
            listenerMode = "debug";
        } else if (getConfig().getBoolean(
                "plugin.overwriteAllNpcDamageListener")) {
            Bukkit.getPluginManager().registerEvents(new DirtyListener(this),
                    this);
            listenerMode = "overwrite";
        } else {
            Bukkit.getPluginManager().registerEvents(
                    new DAntiPvPLeaverListener(this), this);
        }
        this.npcManager = new NPCManager(this);

        enableMetrics(listenerMode);
        enableAutoUpdate();
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
            if (updateMode.equals("off")) {
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
        this.printMessages = config.getBoolean("plugin.printMessages");
        this.spawnOnlyIfPlayerNearby = config
                .getBoolean("npc.spawn.onlyIfPlayerNearby");
        this.distance = config.getInt("npc.spawn.distance");
        this.time = config.getInt("npc.spawn.time");

        this.additionalTimeIfUnderAttack = config
                .getInt("npc.spawn.additionalTimeIfUnderAttack");
        this.broadcastMessageRadius = config
                .getInt("npc.spawn.broadcastMessageRadius");
        this.vanillaExpDrop = config.getBoolean("npc.expdrop");
        this.npcTagNameColor = config.getString("npc.nameTagColor");
        saveConfig();
    }

    public boolean hasVanillaExpDrop() {
        return this.vanillaExpDrop;
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

    public void despawnHumanByName(String playerName) {
        this.npcManager.despawnHumanByName(playerNameToNpcName(playerName));
    }

    public NPC getOneHumanNPCByName(String name) {
        try {
            return this.npcManager.getHumanNPCByName(playerNameToNpcName(name))
                    .get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String playerNameToNpcName(String playername) {
        return playername;
        // return Joiner.on("").join("\u00A7", this.npcTagNameColor, playername);
    }

    public HumanNPC spawnHumanNPC(Player player, Location loc, String name) {
        // TODO: ChatColor for NPC name?
        HumanNPC npc = (HumanNPC) this.npcManager.spawnHumanNPC(
                playerNameToNpcName(name), loc);
        ItemStack[] invContents = player.getInventory().getContents();
        ItemStack[] armourContents = player.getInventory().getArmorContents();
        npc.getInventory().setContents(invContents);
        npc.getInventory().setArmorContents(armourContents);

        // Formula for calculating dropped XP
        int XP = player.getLevel() * 7;
        if (XP > 100) {
            XP = 100;
        }
        npc.setDroppedExp(XP);

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

    public YamlConfiguration getDataFile() {
        return this.dataFile;
    }

    public boolean printMessages() {
        return this.printMessages;
    }

    public static ItemStack setItemNameAndLore(ItemStack item, String name,
            String[] lore) {
        // feature
        return null;
        // CraftItemStack craftItem;
        // if (item instanceof CraftItemStack) {
        // craftItem = (CraftItemStack) item;
        // } else {
        // craftItem = new CraftItemStack(item);
        // }
        //
        // NBTTagCompound tag = craftItem.getHandle().tag;
        // if (tag == null) {
        // tag = new NBTTagCompound();
        // craftItem.getHandle().tag = tag;
        // }
        // NBTTagCompound disp = tag.getCompound("display");
        // if (disp == null) {
        // disp = new NBTTagCompound("display");
        // }
        //
        // disp.setString("Name", name);
        //
        // if (lore != null && lore.length > 0) {
        // NBTTagList list = new NBTTagList("Lore");
        // disp.set("Lore", list);
        // for (String l : lore) {
        // list.add(new NBTTagString("", l));
        // }
        // }
        //
        // tag.setCompound("display", disp);
        //
        // return craftItem;
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
