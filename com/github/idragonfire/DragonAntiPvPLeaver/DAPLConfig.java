package com.github.idragonfire.DragonAntiPvPLeaver;

import org.bukkit.plugin.Plugin;

public class DAPLConfig extends Config {
    @Comment("da1da")
    public boolean plugin_debug = false;
    public boolean plugin_overwriteAllNpcDamageListener = false;
    public boolean plugin_printMessages = true;
//    public final String plugin_update_notify = "notify";
//    public final String plugin_update_automatic = "automaticDownload";
//    public final String plugin_update_none = "off";
    @Comment("dadadad")
    public String plugin_autoupdate = "notify";

    public String npc_expdrop = "vanilla";
    @MultiComment({"alpha", "beta"})
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
    public int npc_spawn_ifhitplayer_monster = 30;

    // languages
    public String language_npcSpawned = "NPC spawned";
    public String language_npcKilled = "<Player>s NPC has been killed while combat logged";
    public String language_yourNpcKilled = "Your NPc has beend killed while combat logged";

    // metric settings
    public boolean metrics_listenerMode = true;
    public boolean metrics_worldGuardUsage = true;
    public boolean metrics_factionsUsage = true;

    public DAPLConfig(Plugin plugin) {
        this.setFile(plugin);
    }
}
