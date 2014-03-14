/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.mutemanager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private final MuteManager plugin;
    Pattern p;

    public MMCommandMute(MuteManager instance) {
        this.plugin = instance;
        p = Pattern.compile("^(\\d+)([mhd])$");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        plugin.logDebug("Command: /mute");
        if (sender instanceof Player) {
            plugin.logDebug("Sender: Player: " + sender.getName());
            if (!sender.hasPermission("mutemanager.mute")) {
                plugin.logDebug("Sender permission NOT OKAY");
                if (!plugin.getMConfig().msgNoPerm().isEmpty()) {
                    sender.sendMessage(plugin.getMConfig().msgNoPerm());
                }
                return true;
            }
            plugin.logDebug("Sender permission OKAY");
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
            plugin.logDebug("M1");
            if (args[1].equalsIgnoreCase("perm")) {
                plugin.logDebug("M2");
                muteTime = 52594900; // 100 Years of minutes.
            } else {
                Matcher m = p.matcher(args[1].toLowerCase());
                if (m.find()) {
                    try {
                        plugin.logDebug("M4");
                        muteTime = Long.parseLong(m.group(1));
                    } catch (NumberFormatException nf) {
                        plugin.logDebug("M5");
                        return false;
                    }
                    if (m.group(2).equals("d")) {
                        plugin.logDebug("Muting for " + m.group(1) + " day(s).");                       
                        muteTime = muteTime * 1440;
                    } else if (m.group(2).equals("h")) {
                        plugin.logDebug("Muting for " + m.group(1) + " hour(s).");                       
                        muteTime = muteTime * 60;
                    }
                } else {
                    try {
                        plugin.logDebug("M6");
                        muteTime = Long.parseLong(args[1]);
                    } catch (NumberFormatException nf) {
                        plugin.logDebug("M7: " + nf.getMessage());
                        return false;
                    }
                }
            }
        } else if (args.length == 1) {
            plugin.logDebug("M8");
            muteTime = plugin.getMConfig().defaultTime();
        } else {
            plugin.logDebug("M9");
            return false;
        }

        String pName = args[0];
        if (pName.equals("*")) {
            plugin.logDebug("C1");
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.logDebug("C2");
                plugin.mutePlayer(player, muteTime, sender, reason);
            }
        } else {
            plugin.logDebug("C3");
            Player player = Bukkit.getPlayerExact(pName);
            plugin.logDebug("C4");
            if (player == null) {
                plugin.logDebug("C5");
                if (plugin.getMConfig().allowOfflineMute()) {
                    plugin.logDebug("C6");
                    plugin.mutePlayer(pName, muteTime, sender, reason);
                } else {
                    plugin.logDebug("C7");
                    if (!plugin.getMConfig().msgNoPlayer().isEmpty()) {
                        plugin.logDebug("C8");
                        sender.sendMessage(plugin.getMConfig().msgNoPlayer());
                    }
                }
            } else {
                plugin.logDebug("C9");
                plugin.mutePlayer(player, muteTime, sender, reason);
                plugin.logDebug("C10");
            }
        }
        plugin.logDebug("C11");
        return true;

    }
}
