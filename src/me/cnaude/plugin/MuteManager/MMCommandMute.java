/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.MuteManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class MMCommandMute implements CommandExecutor {

    private final MM plugin;

    public MMCommandMute(MM instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            if (!sender.hasPermission("mutemanager.mute")) {
                if (!plugin.getMConfig().msgNoPerm().isEmpty()) {
                    sender.sendMessage(plugin.getMConfig().msgNoPerm());
                }
                return true;
            }
        }

        long muteTime;
        
        String reason = "";
        if (args.length > 2) {
            for (int x = 2; x < args.length; x++) {
                reason = reason + " " + args[x];
            }
        }
        if (reason.isEmpty()) {
            reason = plugin.getMConfig().defaultReason();
        }
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("perm")) {
                muteTime = 52594900; // 100 Years of minutes.
            } else {
                try {
                    muteTime = Long.parseLong(args[1]);
                } catch (NumberFormatException nf) {
                    return false;
                }
            }
        } else if (args.length == 1) {
            muteTime = plugin.getMConfig().defaultTime();
        } else {
            return false;
        }
        
        String pName = args[0];
        if (pName.equals("*")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.mutePlayer(player, muteTime, sender, reason);
            }
        } else {
            Player player = Bukkit.getPlayerExact(pName); 
            
            if (player == null) {
                if (plugin.getMConfig().allowOfflineMute()) {
                    plugin.mutePlayer(pName, muteTime, sender, reason);
                } else {
                    if (!plugin.getMConfig().msgNoPlayer().isEmpty()) {
                        sender.sendMessage(plugin.getMConfig().msgNoPlayer());
                    }
                }
            } else {
                plugin.mutePlayer(player, muteTime, sender, reason);
            }
        }
        return true;

    }
}
