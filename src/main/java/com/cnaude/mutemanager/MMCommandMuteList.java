/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.mutemanager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class MMCommandMuteList implements CommandExecutor {

    private final MuteManager plugin;

    public MMCommandMuteList(MuteManager instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            if (!sender.hasPermission("mutemanager.mutelist")) {
                if (!plugin.getMConfig().msgNoPerm().isEmpty()) {
                    sender.sendMessage(plugin.getMConfig().msgNoPerm());
                }
                return true;
            }
        }
        if (plugin.mList.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "[Mute List] [" + ChatColor.WHITE + 0 + ChatColor.YELLOW + "]");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "[Mute List] [" + ChatColor.WHITE + plugin.mList.size() + ChatColor.YELLOW + "]");
            for (MutedPlayer mutedPlayer : plugin.mList) {
                String debugMsg = "";
                if (plugin.getMConfig().debugEnabled()) {
                    debugMsg = ChatColor.GOLD + " [UUID: " + mutedPlayer.getUUID() + "]";
                }
                sender.sendMessage(ChatColor.AQUA + mutedPlayer.getPlayerName() + ChatColor.WHITE + ": " 
                        + ChatColor.YELLOW + mutedPlayer.getExpiredTime(plugin.getMConfig())
                        + ChatColor.RED + " Reason: " + mutedPlayer.getReason()
                        + debugMsg
                );            
            }
        }
        return true;
    }
}