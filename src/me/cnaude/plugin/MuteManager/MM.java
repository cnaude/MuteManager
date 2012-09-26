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

    public void mutePlayer(Player player, Long muteTime, CommandSender sender) {
        long curTime = System.currentTimeMillis();
        long expTime = curTime + (muteTime * 60 * 1000);
        String pName = player.getName();
        mList.put(pName, expTime);
        String senderMessage = ChatColor.AQUA + pName + ChatColor.YELLOW + " is now muted! Duration: " + ChatColor.WHITE + expireTime(pName);
        if (config.shouldNotify()) {
            getServer().broadcastMessage(senderMessage);
        } else {
            sender.sendMessage(senderMessage);
            player.sendMessage(ChatColor.YELLOW + "You have been muted! Duration: " + ChatColor.WHITE + expireTime(pName));
        }
    }
    
    public void mutePlayer(String pName, Long muteTime, CommandSender sender) {
        long curTime = System.currentTimeMillis();
        long expTime = curTime + (muteTime * 60 * 1000);
        mList.put(pName, expTime);
        String senderMessage = ChatColor.AQUA + pName + ChatColor.YELLOW + " is now muted! Duration: " + ChatColor.WHITE + expireTime(pName);
        if (config.shouldNotify()) {
            getServer().broadcastMessage(senderMessage);
        } else {
            sender.sendMessage(senderMessage);            
        }
    }

    public void unMutePlayer(String pName, CommandSender sender) {
        Player player = Bukkit.getServer().getPlayerExact(pName);
        String senderMessage = ChatColor.AQUA + pName + ChatColor.YELLOW + " has been unmuted!";
        boolean success = unMutePlayer(pName);
        if (success) {
            if (config.shouldNotify()) {
                getServer().broadcastMessage(senderMessage);
            } else {
                logInfo(pName + " has been unmuted!");
                if (player != null) {                    
                    player.sendMessage(ChatColor.YELLOW + "You have been unmuted!");
                } 
                sender.sendMessage(senderMessage);
            }            
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Unable to unmute " + ChatColor.AQUA + pName + ChatColor.YELLOW + ".");
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
    
    public boolean isBlockedCmd(String cmd) {
        return getMConfig().blockedCmds().contains(cmd);
    }

    public String expireTime(String pName) {
        DecimalFormat formatter = new DecimalFormat("0.00");
        if (mList.containsKey(pName)) {
            long curTime = System.currentTimeMillis();
            long expTime = mList.get(pName);
            float diffTime = ((expTime - curTime) / 1000f) / 60f;
            if (diffTime > 5256000) {
                return "forever";
            }
            if (diffTime > 525600) {
                return (formatter.format(diffTime / 525600f)) + " years";
            }
            if (diffTime > 1440) {
                return (formatter.format(diffTime / 1440f)) + " days";
            }
            if (diffTime > 60) {
                return (formatter.format(diffTime / 60f)) + " hours";
            }
            if (diffTime < 1f) {
                return (formatter.format(diffTime * 60f)) + " seconds";
            }
            return (formatter.format(diffTime)) + " minutes";
        } else {
            return "0 seconds.";
        }
    }
}
