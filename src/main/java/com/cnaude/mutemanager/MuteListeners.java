package com.cnaude.mutemanager;

import static com.cnaude.mutemanager.MuteManager.config;
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
public class MuteListeners implements Listener {

    private final MuteManager plugin;

    public MuteListeners(MuteManager instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.isMuted(player)) {
            MutedPlayer mutedPlayer = plugin.getMutedPlayer(player);
            if (mutedPlayer == null) {
                return;
            }
            event.setCancelled(true);
            if (!config.msgYouAreMuted().isEmpty()) {
                player.sendMessage(plugin.tokenize(mutedPlayer, config.msgYouAreMuted()));
            }
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (plugin.isMuted(player) && plugin.isBlockedCmd(event.getMessage().split(" "))) {
            MutedPlayer mutedPlayer = plugin.getMutedPlayer(player);
            if (mutedPlayer == null) {
                return;
            }
            event.setCancelled(true);
            plugin.logDebug("Blocking command: " + event.getMessage());
            if (!config.msgYouAreMuted().isEmpty()) {
                player.sendMessage(plugin.tokenize(mutedPlayer, config.msgYouAreMuted()));
            }
            if (plugin.getMConfig().adminListen()) {
                String bCastMessage = ChatColor.WHITE + "[" + ChatColor.RED + "Mute" + ChatColor.WHITE + "]";
                bCastMessage = bCastMessage + "<" + player.getName() + "> ";
                bCastMessage = bCastMessage + ChatColor.GRAY + event.getMessage();
                Bukkit.getServer().broadcast(bCastMessage, plugin.getMConfig().broadcastNode());
            }
        } else {
            plugin.logDebug("Not blocking command: " + event.getMessage());
        }
    }
}
