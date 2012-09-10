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
    
    private boolean shouldNotify;
    private long defaultTime;
    
    public MMConfig(MM instance) {
        plugin = instance;  
        config = this.plugin.getConfig();
        loadValues();
    }
    
    public void loadValues() {               
        shouldNotify = config.getBoolean(GLOBAL_NOTIFY, true);                
        defaultTime = config.getInt(DEFAULT_TIME, 5);                
    }
    
    public boolean shouldNotify() {
        return shouldNotify;
    }
    
    public Long defaultTime() {
        return defaultTime;
    }
    
}
