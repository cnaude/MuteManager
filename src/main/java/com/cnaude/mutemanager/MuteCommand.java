package com.cnaude.mutemanager;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class MuteCommand implements CommandExecutor {

    private final MuteManager plugin;
    Pattern p1;
    Pattern p2;

    public MuteCommand(MuteManager instance) {
        this.plugin = instance;
        p1 = Pattern.compile("^(\\d+)([mhd])$");
        p2 = Pattern.compile("^(\\d+)$");
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
                Matcher m1 = p1.matcher(args[1].toLowerCase());
                Matcher m2 = p2.matcher(args[1]);
                if (m1.find()) {
                    try {
                        plugin.logDebug("M4");
                        muteTime = Long.parseLong(m1.group(1));
                    } catch (NumberFormatException nf) {
                        plugin.logDebug("M5");
                        return false;
                    }
                    switch (m1.group(2)) {
                        case "d":
                            plugin.logDebug("Muting for " + m1.group(1) + " day(s).");
                            muteTime = muteTime * 1440;
                            break;
                        case "h":
                            plugin.logDebug("Muting for " + m1.group(1) + " hour(s).");
                            muteTime = muteTime * 60;
                            break;
                    }
                } else if (m2.find()) {
                    try {
                        plugin.logDebug("M6");
                        muteTime = Long.parseLong(args[1]);
                    } catch (NumberFormatException nf) {
                        plugin.logDebug("M7: " + nf.getMessage());
                        return false;
                    }
                } else {
                    muteTime = plugin.getMConfig().defaultTime();
                    reason = "";
                    for (int x = 1; x < args.length; x++) {
                        reason = reason + " " + args[x];
                    }
                    if (reason.isEmpty()) {
                        reason = plugin.getMConfig().defaultReason();
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
        
        if (muteTime > plugin.getMConfig().maxTime()) {
            sender.sendMessage(plugin.getMConfig().msgMaxReason(muteTime));
            return true;
        }

        final String pName = args[0];
        if (pName.equals("*")) {
            if (!sender.hasPermission("mutemanager.muteall")) {
                sender.sendMessage(plugin.getMConfig().msgNoPerm());
                return true;
            }
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                plugin.mutePlayer(player, muteTime, sender, reason);
            }
        } else {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.getName().equalsIgnoreCase(pName)) {
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
