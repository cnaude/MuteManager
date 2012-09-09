/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.MuteManager;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author cnaude
 */
public class MM extends JavaPlugin {

    // Mute list is stored as playername and milliseconds
    public HashMap<String, Long> mList = new HashMap<String, Long>();
    private final MMListeners mmListeners = new MMListeners(this);
    public boolean configLoaded = false;
    private static MMConfig config;
    public static final String PLUGIN_NAME = "MuteManager";
    public static final String LOG_HEADER = "[" + PLUGIN_NAME + "]";
    static final Logger log = Logger.getLogger("Minecraft");
    private MMFile mFile = new MMFile(this);
    MMLoop mmLoop;

    @Override
    public void onEnable() {
        loadConfig();
        mFile.loadMuteList();
        getCommand("mute").setExecutor(new MMCommandMute(this));
        getCommand("unmute").setExecutor(new MMCommandUnMute(this));
        getCommand("mutelist").setExecutor(new MMCommandMuteList(this));
        getServer().getPluginManager().registerEvents(mmListeners, this);
        mmLoop = new MMLoop(this);
    }

    @Override
    public void onDisable() {
        mmLoop.end();
        mFile.saveMuteList();
        mList.clear();
    }

    void loadConfig() {
        if (!this.configLoaded) {
            getConfig().options().copyDefaults(true);
            saveConfig();
            logInfo("Configuration loaded.");
            config = new MMConfig(this);
        } else {
            reloadConfig();
            getConfig().options().copyDefaults(false);
            config = new MMConfig(this);
            logInfo("Configuration reloaded.");
        }
        configLoaded = true;
    }

    public void logInfo(String _message) {
        log.log(Level.INFO, String.format("%s %s", LOG_HEADER, _message));
    }

    public void logError(String _message) {
        log.log(Level.SEVERE, String.format("%s %s", LOG_HEADER, _message));
    }

    public MMConfig getMConfig() {
        return config;
    }

    public void mutePlayer(Player player, Integer muteTime, CommandSender sender) {
        mutePlayer(player.getName(), muteTime, sender);
        player.sendMessage(ChatColor.YELLOW + "You have been muted for " + ChatColor.WHITE + muteTime + ChatColor.YELLOW + " minutes!");
    }
    
    public void mutePlayer(OfflinePlayer player, Integer muteTime, CommandSender sender) {
        mutePlayer(player.getName(), muteTime, sender);
    }
    
    public void mutePlayer(String pName, Integer muteTime, CommandSender sender) {        
        long curTime = System.currentTimeMillis();
        long expTime = curTime + (muteTime * 60 * 1000);
        mList.put(pName, expTime);        
        String senderMessage = ChatColor.AQUA + pName + ChatColor.YELLOW + " has been muted for " + ChatColor.WHITE + muteTime + ChatColor.YELLOW + " minutes!";
        if (config.shouldNotify()) {
            getServer().broadcastMessage(senderMessage);
        } else {
            sender.sendMessage(senderMessage);
        }
    }
    
    public void unMutePlayer(Player player, CommandSender sender) {        
        String pName = player.getName();
        String senderMessage = ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has been unmuted!";
        if (mList.containsKey(pName)) {
            mList.remove(pName);
            player.sendMessage(ChatColor.YELLOW + "You are no longer muted!");
            if (config.shouldNotify()) {
               getServer().broadcastMessage(senderMessage);
            } else {
                sender.sendMessage(senderMessage);
                logInfo(player.getName() + " has been unmuted!");
            }
        }        
    }
    
    public void unMutePlayer(String pName, CommandSender sender) { 
        Player player = Bukkit.getPlayerExact(pName);
        
        if (unMutePlayer(pName)) {
            if (player instanceof Player) {
                player.sendMessage(ChatColor.YELLOW + "You are no longer muted!");
            }
            String senderMessage = ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has been unmuted!";
            if (config.shouldNotify()) {
               getServer().broadcastMessage(senderMessage);
            } else {
                sender.sendMessage(senderMessage);
                logInfo(player.getName() + " has been unmuted!");
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Unable to unmute " + ChatColor.AQUA + pName);
        }                                   
    }
    
    public boolean unMutePlayer(String pName) {
        if (mList.containsKey(pName)) {
            mList.remove(pName);
            return true;
        } else {
            return false;
        }
    }

    public boolean isMuted(Player player) {
        String pName = player.getName();
        if (mList.containsKey(pName)) {
            long curTime = System.currentTimeMillis();
            long expTime = mList.get(pName);
            if (expTime > curTime) {
                return true;
            } else {
                unMutePlayer(pName);
                return false;
            }
        } else {
            return false;
        }
    }

    public String expireTime(Player player) {
        String pName = player.getName();
        return expireTime(pName);
    }

    public String expireTime(String pName) {
        DecimalFormat formatter = new DecimalFormat("0.00");                
        if (mList.containsKey(pName)) {
            long curTime = System.currentTimeMillis();
            long expTime = mList.get(pName);
            float diffTime = ((expTime - curTime) / 1000f) / 60f;
            return (formatter.format(diffTime)) + " minutes";
        } else {
            return "0 seconds.";
        }        
    }
}
