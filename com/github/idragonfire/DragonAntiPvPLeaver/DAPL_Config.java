package com.github.idragonfire.DragonAntiPvPLeaver;

import org.bukkit.plugin.Plugin;

public class DAPL_Config extends Config {

    public boolean plugin_debug = false;
    public boolean plugin_overwriteAllNpcDamageListener = false;
    @MultiComment( { "some plugins, like AuthMe protects the NPC",
            "if your NPC take no damage set these value to true",
            "WARNING: everybody can kill the NPC if these value is true" })
    public boolean plugin_printMessages = true;
    public final String plugin_update_notify = "notify";
    public final String plugin_update_automatic = "automaticDownload";
    public final String plugin_update_none = "off";
    @Comment("options: " + plugin_update_notify + "," + plugin_update_automatic
            + "," + plugin_update_none)
    public String plugin_autoupdate = plugin_update_notify;

    public final String npc_expdrop_vanilla = "vanilla";
    public final String npc_expdrop_none = "off";
    @Comment("options: " + npc_expdrop_vanilla + "," + npc_expdrop_none)
    public String npc_expdrop = "vanilla";
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
    @Comment("activate/deactivate metrics graphs opt-out")
    public boolean metrics_listenerMode = true;
    public boolean metrics_worldGuardUsage = true;
    public boolean metrics_factionsUsage = true;

    public DAPL_Config(Plugin plugin) {
        this.setFile(plugin);
    }
}