/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.MuteManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class MMCommands implements CommandExecutor {
    
    String description = "Mutes a player for specified amount of time.";
    String usageMessage = "/mute [player] [minutes]";
     
    private final MM plugin;
    
    public MMCommands(MM instance) {
        this.plugin = instance;
    }
    
    @Override
     public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            if (!sender.hasPermission("mutemanager.mute")) {
                return true;
            }
        }
         if (args.length < 2)  {
             sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
             return false;
         }
 
         Player player = Bukkit.getPlayerExact(args[0]);
 
         // If a player is hidden from the sender pretend they are offline
         if (player == null || (sender instanceof Player && !((Player) sender).canSee(player))) {
             sender.sendMessage("There's no player by that name online.");
         } else {
             int muteTime;
             try {
                muteTime = Integer.parseInt(args[1]);
                plugin.mutePlayer(player, muteTime);
             }
             catch (NumberFormatException nf) {
                 sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                 return false;
             }
             
             /*
             if (sender instanceof ConsoleCommandSender) {
                 Bukkit.getLogger().log(Level.INFO, "[{0}->{1}] {2}", new Object[]{sender.getName(), player.getName(), message});
                 Bukkit.getLogger().info(result);
             }
             
             player.sendMessage(result);
             sender.sendMessage(ChatColor.GREEN + "-> " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + ": " + ChatColor.GRAY + message);
             * */
         }
 
         return true;
     }

 
}
