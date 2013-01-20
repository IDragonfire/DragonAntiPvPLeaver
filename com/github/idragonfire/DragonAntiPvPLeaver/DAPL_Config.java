package com.github.idragonfire.DragonAntiPvPLeaver;


import org.bukkit.plugin.Plugin;

import com.github.idragonfire.DragonAntiPvPLeaver.util.Comment;
import com.github.idragonfire.DragonAntiPvPLeaver.util.Config;
import com.github.idragonfire.DragonAntiPvPLeaver.util.MultiComment;

public class DAPL_Config extends Config {

    // internal const
    public final String npc_spawn_bypass = "dragonantipvpleaver.bypass";

    public final String plugin_update_notify = "notify";
    public final String plugin_update_automatic = "automaticDownload";
    public final String plugin_update_none = "off";

    public final String npc_expdrop_vanilla = "vanilla";
    public final String npc_expdrop_none = "off";

    // plugin section
    public boolean plugin_debug = false;
    public boolean plugin_overwriteAllNpcDamageListener = false;
    @MultiComment( { "some plugins, like AuthMe protects the NPC",
            "if your NPC take no damage set these value to true",
            "WARNING: everybody can kill the NPC if these value is true" })
    public boolean plugin_printMessages = true;

    @Comment("options: " + plugin_update_notify + "," + plugin_update_automatic
            + "," + plugin_update_none)
    public String plugin_autoupdate = plugin_update_notify;

    @Comment("options: " + npc_expdrop_vanilla + "," + npc_expdrop_none)
    public String npc_expdrop = "vanilla";
    public boolean npc_weararmor = true;
    public int npc_broadcastMessageRadius = 32;
    public String npc_spawnmode = "always";
    public int npc_additionalTimeIfUnderAttack = 15;

    // spawn modes
    public boolean npc_spawn_always_active = false;
    public int npc_spawn_always_time = 10;

    public boolean npc_spawn_playernearby_active = true;
    public int npc_spawn_playernearby_distance = 8;
    public int npc_spawn_playernearby_time = 10;

    public boolean npc_spawn_monsternearby_active = true;
    public int npc_spawn_monsternearby_distance = 8;
    public int npc_spawn_monsternearby_time = 10;

    public boolean npc_spawn_underattackfromMonsters_active = true;
    public int npc_spawn_underattackfromMonsters_time = 10;

    public boolean npc_spawn_underattackfromplayers_active = true;
    public int npc_spawn_underattackfromplayers_time = 10;

    public boolean npc_spawn_ifhitplayer_active = true;
    public int npc_spawn_ifhitplayer_time = 20;

    public boolean npc_spawn_ifhitmonster_active = true;
    public int npc_spawn_ifhitmonster_time = 30;

    // money options
    public boolean lossmoney_active = false;
    public int lossmoney_value = 0;
    public int lossmoney_percentage = 75;

    // factions options
    public boolean factions_losepower_active = false;
    public int factions_losepower_percentage = 100;

    // mcmmo options
    public boolean mcmmo_loselevel_active = false;
    public int mcmmo_loselevel_percentage = 100;

    // Jobs options
    public boolean jobs_loselevel_active = false;
    public boolean jobs_loselevel_fromAllJobs = true;
    public int jobs_loselevel_value = 1;

    // Command blocker
    public boolean pvp_blockcommands_active = true;
    public boolean pvp_blockcommands_whitelist_active = true;
    public String[] pvp_blockcommands_whitelist_cmds = new String[] { "help" };
    public boolean pvp_blockcommands_blacklist_active = true;
    public String[] pvp_blockcommands_blacklist_cmds = new String[] { "set home" };

    // languages
    public String language_npcSpawned = "NPC spawned";
    public String language_npcKilled = "<Player>s NPC has been killed while combat logged";
    public String language_yourNpcKilled = "Your NPc has beend killed while combat logged";

    // metric settings
    @Comment("activate/deactivate metrics graphs opt-out")
    public boolean metrics_listenerMode = true;
    public boolean metrics_worldGuardUsage = true;
    public boolean metrics_factionsUsage = true;

    public DAPL_Config(Plugin plugin) {
        setFile(plugin);
    }
}
