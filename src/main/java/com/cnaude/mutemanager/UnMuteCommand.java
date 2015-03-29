package com.cnaude.mutemanager;

import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class UnMuteCommand implements CommandExecutor {

    private final MuteManager plugin;

    public UnMuteCommand(MuteManager instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            if (!sender.hasPermission("mutemanager.unmute")) {
                if (!plugin.getMConfig().msgNoPerm().isEmpty()) {
                    sender.sendMessage(plugin.getMConfig().msgNoPerm());
                }
                return true;
            }
        }

        if (args.length == 1) {
            String pName = args[0];
            if (pName.equals("*")) {
                ArrayList<String> unMuteList = new ArrayList<>();
                for (MutedPlayer mutedPlayer : plugin.muteList) {
                        unMuteList.add(mutedPlayer.getPlayerName());
                }
                for (String pNameInner : unMuteList) {
                    plugin.unMutePlayer(pNameInner, sender);
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
