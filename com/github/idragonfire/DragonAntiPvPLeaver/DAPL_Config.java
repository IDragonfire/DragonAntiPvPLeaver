package com.github.idragonfire.DragonAntiPvPLeaver;

import org.bukkit.plugin.Plugin;

import com.github.idragonfire.DragonAntiPvPLeaver.util.Comment;
import com.github.idragonfire.DragonAntiPvPLeaver.util.Config;

public class DAPL_Config extends Config {

    // internal const
    public final String npc_spawn_bypass = "dragonantipvpleaver.bypass";
    public final String pvp_blockcommands_bypass = "dragonantipvpleaver.blockcommands.bypass";

    public final String npc_expdrop_vanilla = "vanilla";
    public final String npc_expdrop_none = "none";

    // plugin section
    public boolean plugin_debug = false;
    public boolean plugin_printMessages = true;

    @Comment("options: " + npc_expdrop_vanilla + "," + npc_expdrop_none)
    public String npc_expdrop = "vanilla";
    public boolean npc_weararmor = true;
    public int npc_broadcastMessageRadius = 32;
    public int npc_additionalTimeIfUnderAttack = 15;

    // spawn modes
    public boolean npc_spawn_always_active = false;
    public int npc_spawn_always_time = 10;

    public boolean npc_spawn_playernearby_active = true;
    public int npc_spawn_playernearby_distance = 8;
    public int npc_spawn_playernearby_lifetime = 10;

    public boolean npc_spawn_monsternearby_active = true;
    public int npc_spawn_monsternearby_distance = 8;
    public int npc_spawn_monsternearby_lifetime = 10;
    public int npc_spawn_monsternearby_cooldown = 20;

    public boolean npc_spawn_underattackfromMonsters_active = true;
    public int npc_spawn_underattackfromMonsters_lifetime = 10;
    public int npc_spawn_underattackfromMonsters_cooldown = 20;

    public boolean npc_spawn_underattackfromPlayers_active = true;
    public int npc_spawn_underattackfromPlayers_lifetime = 10;
    public int npc_spawn_underattackfromPlayers_cooldown = 10;

    public boolean npc_spawn_ifhitPlayer_active = true;
    public int npc_spawn_ifhitPlayer_lifetime = 20;
    public int npc_spawn_ifhitPlayer_cooldown = 20;

    public boolean npc_spawn_ifhitMonster_active = true;
    public int npc_spawn_ifhitMonster_lifetime = 30;
    public int npc_spawn_ifhitMonster_cooldown = 30;

    // money options
    public boolean lossmoney_active = false;
    public int lossmoney_value = 0;
    public int lossmoney_percentage = 75;

    // factions options
    @Comment("Player lose normal power on death, you can activate extra power lose")
    public boolean factions_extraLosePowerActive = false;
    @Comment("a value between -10 to 10")
    public double factions_losePowerDelta = -3.0;

    // mcmmo options
    public boolean mcmmo_loselevel_active = false;
    public int mcmmo_loselevel_percentage = 100;

    // Jobs options
    public boolean jobs_loselevel_active = false;
    public boolean jobs_loselevel_fromAllJobs = true;
    public int jobs_loselevel_value = 1;

    // Command blocker
    public boolean pvp_blockcommands_active = true;
    public int pvp_blockcommands_cooldown_underattack = 20;
    public int pvp_blockcommands_cooldown_ifhit = 20;
    @Comment("Allows only commands in whitelist, you MUST set blacklist.active = false")
    public boolean pvp_blockcommands_whitelist_active = true;
    public String[] pvp_blockcommands_whitelist_cmds = new String[] { "help" };
    @Comment("Allows all commands expect in the blacklist, you MUST set whitelist.active = false")
    public boolean pvp_blockcommands_blacklist_active = false;
    public String[] pvp_blockcommands_blacklist_cmds = new String[] { "set home" };

    // languages
    public String language_npcSpawned = "NPC spawned";
    public String language_npcKilled = "<Player>s NPC has been killed while combat logged";
    public String language_yourNpcKilled = "Your NPC has beend killed while combat logged";
    public String language_commandBlocked = "DAPL: These command has been blocked.";

    // metric settings
    @Comment("activate/deactivate metrics graphs opt-out")
    public boolean metrics_listenerMode = true;
    public boolean metrics_worldGuardUsage = true;
    public boolean metrics_factionsUsage = true;

    public DAPL_Config(Plugin plugin) {
        setFile(plugin);
    }
}
