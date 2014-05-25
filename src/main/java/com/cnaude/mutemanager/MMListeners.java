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
public class MMListeners implements Listener {

    private final MuteManager plugin;

    public MMListeners(MuteManager instance) {
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
                player.sendMessage(config.msgYouAreMuted()
                        .replace("%DURATION%", mutedPlayer.getExpiredTime(config))
                        .replace("%REASON%", mutedPlayer.getReason())
                );
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String attemptedCmd = event.getMessage().split(" ")[0];
        if (plugin.isMuted(player) && plugin.isBlockedCmd(attemptedCmd)) {
            MutedPlayer mutedPlayer = plugin.getMutedPlayer(player);
            if (mutedPlayer == null) {
                return;
            }
            event.setCancelled(true);
            if (!config.msgYouAreMuted().isEmpty()) {                
                player.sendMessage(config.msgYouAreMuted()
                        .replace("%DURATION%", mutedPlayer.getExpiredTime(config))
                        .replace("%REASON%", mutedPlayer.getReason())
                );
            }
            if (plugin.getMConfig().adminListen()) {
                String bCastMessage = ChatColor.WHITE + "[" + ChatColor.RED + "Mute" + ChatColor.WHITE + "]";
                bCastMessage = bCastMessage + "<" + player.getName() + "> ";
                bCastMessage = bCastMessage + ChatColor.GRAY + event.getMessage();
                Bukkit.getServer().broadcast(bCastMessage, plugin.getMConfig().broadcastNode());
            }
        }
    }
}
