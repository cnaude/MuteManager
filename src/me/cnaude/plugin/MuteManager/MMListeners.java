/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.MuteManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
            player.sendMessage(ChatColor.YELLOW + "You are " + ChatColor.RED + "muted" + ChatColor.YELLOW + "! Expires in " + plugin.expireTime(player));
        }
    }
}
