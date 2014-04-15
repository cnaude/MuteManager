/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.mutemanager;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
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
    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
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
                    switch (m.group(2)) {
                        case "d":
                            plugin.logDebug("Muting for " + m.group(1) + " day(s).");
                            muteTime = muteTime * 1440;
                            break;
                        case "h":
                            plugin.logDebug("Muting for " + m.group(1) + " hour(s).");
                            muteTime = muteTime * 60;
                            break;
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

        final String pName = args[0];
        if (pName.equals("*")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.mutePlayer(player, muteTime, sender, reason);
            }
        } else {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.getName().equals(pName)) {
                    plugin.mutePlayer(player, muteTime, sender, reason);
                    return true;
                }
            }
            if (plugin.getMConfig().allowOfflineMute()) {
                final long finalMuteTime = muteTime;
                final String finalReason = reason;
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(pName));
                            Map<String, UUID> response = fetcher.call();
                            for (UUID uuid : response.values()) {
                                plugin.mutePlayer(pName, uuid, finalMuteTime, sender, finalReason);
                            }
                        } catch (Exception e) {
                            plugin.logError("Exception while running UUIDFetcher: " + pName);
                            plugin.logError(e.getMessage());
                        }
                    }
                });
            }
        }

        return true;
    }
}
