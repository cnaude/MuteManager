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
public class MMCommandUnMute implements CommandExecutor {

    private final MM plugin;

    public MMCommandUnMute(MM instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 1) {
            Player player = Bukkit.getPlayerExact(args[0]);

            // If a player is hidden from the sender pretend they are offline
            if (player == null || (sender instanceof Player && !((Player) sender).canSee(player))) {
                sender.sendMessage("There's no player by that name online.");
            } else {
                plugin.unMutePlayer(player, sender);
            }
            return true;
        } else {
            return false;
        }
    }
}
