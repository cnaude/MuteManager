/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.MuteManager;

import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.Configuration;

/**
 *
 * @author cnaude
 */
public final class MMConfig {
    private final Configuration config;
    private final MM plugin;
    
    private static final String GLOBAL_NOTIFY            = "Global.Notify";
    private static final String DEFAULT_TIME             = "Global.DefaultTime";
    private static final String DEFAULT_REASON           = "Global.DefaultReason";
    private static final String ADMIN_LISTEN             = "Global.AdminListen";
    private static final String BROADCAST_NODE           = "Global.BroadcastNode";
    private static final String COMMANDS                 = "Global.Commands";
    private static final String BLOCK_CMDS               = "Global.BlockCommands";
    private static final String ALLOW_OFFLINE_MUTE       = "Global.AllowOfflineMute";
    
    private boolean shouldNotify;
    private long defaultTime;
    private String defaultReason;
    private boolean adminListen;
    private String broadcastNode;   
    private List<String> blockedCommands = Arrays.asList();    
    private boolean allowOfflineMute;
    
    public MMConfig(MM instance) {
        plugin = instance;  
        config = this.plugin.getConfig();
        loadValues();
    }
    
    public void loadValues() {               
        shouldNotify  = config.getBoolean(GLOBAL_NOTIFY, true);                
        defaultTime   = config.getInt(DEFAULT_TIME, 5);       
        defaultReason = config.getString(DEFAULT_REASON, "None");
        adminListen   = config.getBoolean(ADMIN_LISTEN, false);
        broadcastNode = config.getString(BROADCAST_NODE, "mutemanager.listen");
        if (config.getBoolean(BLOCK_CMDS, false)) {
            blockedCommands = config.getStringList(COMMANDS);
        } 
        allowOfflineMute  = config.getBoolean(ALLOW_OFFLINE_MUTE, false);                
    }
    
    public boolean shouldNotify() {
        return shouldNotify;
    }
    
    public Long defaultTime() {
        return defaultTime;
    }
    
    public String defaultReason() {
        return defaultReason;
    }
    
    public boolean adminListen() {
        return adminListen;
    }
    
    public String broadcastNode() {
        return broadcastNode;
    }
    
    public List<String> blockedCmds() {
        return blockedCommands;
    }

    public boolean allowOfflineMute() {
        return allowOfflineMute;
    }
}
