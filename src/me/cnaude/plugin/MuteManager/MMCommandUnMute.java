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
                Set st = plugin.mList.keySet();
                Iterator itr = st.iterator();
                while(itr.hasNext()) {                
                    String pNameInner = (String)itr.next();                    
                    if (plugin.mList.containsKey(pNameInner)) {
                        unMuteList.add(pNameInner);                       
                    }
                }
                for (Object pNameInner : unMuteList) {
                    plugin.unMutePlayer((String)pNameInner); 
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
