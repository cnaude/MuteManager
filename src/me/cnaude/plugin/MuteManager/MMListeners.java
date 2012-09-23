/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.MuteManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author cnaude
 */
public class MMListeners implements Listener {

    private final MM plugin;

    public MMListeners(MM instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.isMuted(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.YELLOW + "You are " + ChatColor.RED + "muted" + ChatColor.YELLOW + "! Duration: " + ChatColor.WHITE + plugin.expireTime(player));
            if (plugin.getMConfig().adminListen()) {
                String bCastMessage = ChatColor.WHITE + "[" + ChatColor.RED + "Mute" + ChatColor.WHITE + "]";
                bCastMessage = bCastMessage + "<" + player.getName() + "> ";
                bCastMessage = bCastMessage + ChatColor.GRAY + event.getMessage();
                Bukkit.getServer().broadcast(bCastMessage, plugin.getMConfig().broadcastNode());
            }
        } else {
            plugin.unMutePlayer(player.getName());
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocessEvent (PlayerCommandPreprocessEvent  event) {
        Player player = event.getPlayer();
        String attemptedCmd = event.getMessage().split(" ")[0];        
        if (plugin.isMuted(player) && plugin.isBlockedCmd(attemptedCmd)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.YELLOW + "You are " + ChatColor.RED + "muted" + ChatColor.YELLOW + "! Duration: " + ChatColor.WHITE + plugin.expireTime(player));
            if (plugin.getMConfig().adminListen()) {
                String bCastMessage = ChatColor.WHITE + "[" + ChatColor.RED + "Mute" + ChatColor.WHITE + "]";
                bCastMessage = bCastMessage + "<" + player.getName() + "> ";
                bCastMessage = bCastMessage + ChatColor.GRAY + event.getMessage();
                Bukkit.getServer().broadcast(bCastMessage, plugin.getMConfig().broadcastNode());
            }
        } 
    }
}
