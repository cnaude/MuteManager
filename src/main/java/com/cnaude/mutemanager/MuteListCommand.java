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
public class MuteListCommand implements CommandExecutor {

    private final MuteManager plugin;

    public MuteListCommand(MuteManager instance) {
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
        if (plugin.muteList.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "[Mute List] [" + ChatColor.WHITE + 0 + ChatColor.YELLOW + "]");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "[Mute List] [" + ChatColor.WHITE + plugin.muteList.size() + ChatColor.YELLOW + "]");
            for (MutedPlayer mutedPlayer : plugin.muteList) {
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