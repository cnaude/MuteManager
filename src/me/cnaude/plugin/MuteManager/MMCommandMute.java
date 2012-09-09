/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.MuteManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return true;
            }
        }

        int muteTime;
        
        if (args.length == 2) {
            try {
                muteTime = Integer.parseInt(args[1]);
            } catch (NumberFormatException nf) {
                return false;
            }
        } else if (args.length == 1) {
            muteTime = plugin.getMConfig().defaultTime();
        } else {
            return false;
        }
        
        Player player = Bukkit.getPlayerExact(args[0]);  
        if (player == null) {
            sender.sendMessage("There's no player by that name online.");
        } else {
            plugin.mutePlayer(player, muteTime, sender);
        }
        return true;

    }
}
