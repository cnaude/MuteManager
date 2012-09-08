/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.MuteManager;

import java.util.HashMap;
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

    @Override
    public void onEnable() {
        registerCommand("mute");
        registerCommand("unmute");
        getServer().getPluginManager().registerEvents(mmListeners, this);
    }
    
    public void mutePlayer(Player player, Integer muteTime) {
        String pName = player.getName();
        long curTime = System.currentTimeMillis();
        long expTime = curTime + (muteTime * 60 * 1000);
        mList.put(pName, expTime);
    }
    
    public void unMutePlayer(Player player) {
        String pName = player.getName();
        if (mList.containsKey(pName)) {
            mList.remove(pName);
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
                mList.remove(pName);
                return false;
            }
        } else {
            return false;
        }                   
    }
    
    public String expireTime(Player player) {
        String pName = player.getName();
        if (mList.containsKey(pName)) {
            long curTime = System.currentTimeMillis();
            long expTime = mList.get(pName);
            long diffTime = (expTime - curTime) / 1000;
            return (String.valueOf(diffTime)) + " seconds";
        } else {
            return "0 seconds.";
        }     
    }

    private void registerCommand(String command) {
        try {
            getCommand(command).setExecutor(new MMCommands(this));
        } catch (Exception ex) {
            System.out.println("Failed to register command '" + command + "'! Is it allready used by some other Plugin? " + ex.getMessage());
        }
    }
}
