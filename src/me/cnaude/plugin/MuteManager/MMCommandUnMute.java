/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.MuteManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
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
public class MMCommandUnMute implements CommandExecutor {

    private final MM plugin;

    public MMCommandUnMute(MM instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            if (!sender.hasPermission("mutemanager.unmute")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return true;
            }
        }

        if (args.length == 1) {    
            String pName = args[0];
            if (pName.equals("*")) {
                ArrayList unMuteList = new ArrayList();            
                for (String pNameInner : plugin.mList.keySet()) {                                  
                        unMuteList.add(pNameInner);                       
                }
                for (Object pNameInner : unMuteList) {
                    plugin.unMutePlayer((String)pNameInner, sender); 
                }
                unMuteList.clear();
            } else {
                plugin.unMutePlayer(pName, sender);
            }
            return true;
        } else {
            return false;
        }
    }
}
