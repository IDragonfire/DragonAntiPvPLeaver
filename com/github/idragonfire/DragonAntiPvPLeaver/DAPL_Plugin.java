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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.idragonfire.DragonAntiPvPLeaver.api.DDamagerListenerHandler;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DFakePlayerManager;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DPlayerListener;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DPlugin;
import com.github.idragonfire.DragonAntiPvPLeaver.api.DSpawnCheckerManager;
import com.github.idragonfire.DragonAntiPvPLeaver.deathfeatures.FactionsLosePower;
import com.github.idragonfire.DragonAntiPvPLeaver.deathfeatures.MoneyLose;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.CommandDamageListener;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.DamageListenerHandler;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.Listener_Debug;
import com.github.idragonfire.DragonAntiPvPLeaver.listener.Listener_Normal;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.Always;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.FactionSupport;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.IfHit;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.NearBy;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.UnderAttack;
import com.github.idragonfire.DragonAntiPvPLeaver.spawn.checker.WorldGuardSupport;
import com.github.idragonfire.DragonAntiPvPLeaver.util.Metrics;
import com.github.idragonfire.DragonAntiPvPLeaver.util.Metrics.Graph;
import com.github.idragonfire.DragonAntiPvPLeaver.util.Metrics.Plotter;

public class DAPL_Plugin extends JavaPlugin implements Listener, DPlugin {
	protected List<String> deadPlayers;
	protected YamlConfiguration dataFile;
	protected DFakePlayerManager npcManager;
	protected SpawnCheckerManager manager;
	protected DamageListenerHandler damagerListenerHandler;
	protected Listener_Normal listener;
	public DAPL_Config config;

	public enum DAMAGE_MODE {
		MONSTER, HUMANS
	}

	@Override
	public void onEnable() {
		npcManager = new DAPL_Human_Manager(this);
		deadPlayers = new ArrayList<String>();
		loadConfig();
		loadDeadPlayers();

		// set listener mode
		String listenerMode = "normal";
		Listener_Normal listener = null;
		if (getConfig().getBoolean("plugin.debug")) {
			listener = new Listener_Debug(getLogger());
			listenerMode = "debug";
		} else {
			listener = new Listener_Normal();
		}
		this.listener = listener;

		listener.init(config, npcManager);
		initListener(listener);
		initDeathFeatures();
		Bukkit.getPluginManager().registerEvents(listener, this);

		enableMetrics(listenerMode);
		// enableAutoUpdate();

		Bukkit.getPluginManager().registerEvents(this, this);
	}

	private void initDeathFeatures() {
		if (config.factions_extraLosePowerActive) {
			FactionsLosePower factionsDeathFeatures = new FactionsLosePower(
					config.factions_losePowerDelta);
			if (factionsDeathFeatures.validDeathListener()) {
				addDaplPlayerListener(factionsDeathFeatures);
			}
		}
		if (config.lossmoney_active) {
			MoneyLose moneyLoseFeature = new MoneyLose(config.lossmoney_value);
			if (moneyLoseFeature.validDeathListener()) {
				addDaplPlayerListener(moneyLoseFeature);
			}
		}
	}

	private void initListener(Listener_Normal listener) {
		// Deal Damage Listener
		HashMap<DAMAGE_MODE, DamageTrackerConfig> dealerConfig = new HashMap<DAMAGE_MODE, DamageTrackerConfig>();
		if (config.npc_spawn_ifhitMonster_active) {
			dealerConfig.put(DAMAGE_MODE.MONSTER, new DamageTrackerConfig(
					config.npc_spawn_ifhitMonster_lifetime,
					config.npc_spawn_ifhitMonster_cooldown));
		}
		if (config.npc_spawn_ifhitPlayer_active) {
			dealerConfig.put(DAMAGE_MODE.HUMANS, new DamageTrackerConfig(
					config.npc_spawn_ifhitPlayer_lifetime,
					config.npc_spawn_ifhitPlayer_cooldown));
		}

		// Take Damage Listener
		HashMap<DAMAGE_MODE, DamageTrackerConfig> takerConfig = new HashMap<DAMAGE_MODE, DamageTrackerConfig>();
		if (config.npc_spawn_underattackfromMonsters_active) {
			takerConfig.put(DAMAGE_MODE.MONSTER, new DamageTrackerConfig(
					config.npc_spawn_underattackfromMonsters_lifetime,
					config.npc_spawn_underattackfromMonsters_cooldown));
		}
		if (config.npc_spawn_underattackfromPlayers_active) {
			takerConfig.put(DAMAGE_MODE.HUMANS, new DamageTrackerConfig(
					config.npc_spawn_underattackfromPlayers_lifetime,
					config.npc_spawn_underattackfromPlayers_cooldown));
		}
		damagerListenerHandler = new DamageListenerHandler();
		// init spawn modes
		manager = new SpawnCheckerManager(config);
		if (config.npc_spawn_always_active) {
			manager.addWhiteListChecker(new Always(config));
			getLogger().log(Level.INFO, "spawn mode: always");
		} else {
			if (config.npc_spawn_playernearby_active) {
				manager.addWhiteListChecker(new NearBy(
						config.npc_spawn_playernearby_distance,
						HumanEntity.class,
						config.npc_spawn_playernearby_lifetime));
			}
			if (config.npc_spawn_monsternearby_active) {
				manager.addWhiteListChecker(new NearBy(
						config.npc_spawn_monsternearby_distance, Monster.class,
						config.npc_spawn_monsternearby_lifetime));
			}

			if (config.npc_spawn_underattackfromMonsters_active
					|| config.npc_spawn_underattackfromPlayers_active) {
				UnderAttack underAttackListener = new UnderAttack(takerConfig);
				manager.addWhiteListChecker(underAttackListener);
				damagerListenerHandler
						.addAttackVictionListener(underAttackListener);
			}

			if (config.npc_spawn_ifhitMonster_active
					|| config.npc_spawn_ifhitPlayer_active) {
				IfHit ifHitListener = new IfHit(dealerConfig);
				manager.addWhiteListChecker(ifHitListener);
				damagerListenerHandler.addAttackVictionListener(ifHitListener);
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
		// set block commands listener
		if (config.pvp_blockcommands_active) {
			CommandDamageListener cmdBlockListener = new CommandDamageListener(
					config, getLogger());
			damagerListenerHandler.addAttackVictionListener(cmdBlockListener);
			Bukkit.getPluginManager().registerEvents(cmdBlockListener, this);
		}

		// set injection if necessary
		if (damagerListenerHandler.hasRegisteredListeners()) {
			listener.setListenerInjection(damagerListenerHandler);
		}

		listener.setSpawnChecker(manager);
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

	@Override
	public void addDaplPlayerListener(DPlayerListener listener) {
		npcManager.addDaplPlayerListener(listener);
	}

	@Override
	public void removeDaplPlayerListener(DPlayerListener listener) {
		npcManager.removeDaplPlayerListener(listener);
	}

	@Override
	public DSpawnCheckerManager getSpawnChecker() {
		return manager;
	}

	@Override
	public DDamagerListenerHandler getDamagerListenerHandler() {
		return damagerListenerHandler;
	}
}