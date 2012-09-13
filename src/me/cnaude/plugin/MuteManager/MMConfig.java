/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.MuteManager;

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
    private static final String ADMIN_LISTEN             = "Global.AdminListen";
    private static final String BROADCAST_NODE           = "Global.BroadcastNode";
    
    private boolean shouldNotify;
    private long defaultTime;
    private boolean adminListen;
    private String broadcastNode;    
    
    public MMConfig(MM instance) {
        plugin = instance;  
        config = this.plugin.getConfig();
        loadValues();
    }
    
    public void loadValues() {               
        shouldNotify  = config.getBoolean(GLOBAL_NOTIFY, true);                
        defaultTime   = config.getInt(DEFAULT_TIME, 5);                
        adminListen   = config.getBoolean(ADMIN_LISTEN, false);
        broadcastNode = config.getString(BROADCAST_NODE, "mutemanager.listen");
    }
    
    public boolean shouldNotify() {
        return shouldNotify;
    }
    
    public Long defaultTime() {
        return defaultTime;
    }
    
    public boolean adminListen() {
        return adminListen;
    }
    
    public String broadcastNode() {
        return broadcastNode;
    }
    
}
