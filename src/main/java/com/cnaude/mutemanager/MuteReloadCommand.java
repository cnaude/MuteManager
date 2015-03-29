package com.cnaude.mutemanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class MuteReloadCommand implements CommandExecutor {

    private final MuteManager plugin;

    public MuteReloadCommand(MuteManager instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
        plugin.logDebug("Command: /mutereload");
        if (sender instanceof Player) {
            plugin.logDebug("Sender: Player: " + sender.getName());
            if (!sender.hasPermission("mutemanager.reload")) {
                plugin.logDebug("Sender permission NOT OKAY");
                if (!plugin.getMConfig().msgNoPerm().isEmpty()) {
                    sender.sendMessage(plugin.getMConfig().msgNoPerm());
                }
                return true;
            }
            plugin.logDebug("Sender permission OKAY");
        }

        plugin.loadConfig(sender);

        return true;
    }
}
