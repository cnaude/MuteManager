package com.cnaude.mutemanager;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author cnaude
 */
public class MuteManager extends JavaPlugin {

    // Mute list is stored as playername and milliseconds
    public ArrayList<MutedPlayer> muteList = new ArrayList<>();
    private final MuteListeners mmListeners = new MuteListeners(this);
    public boolean configLoaded = false;
    public static MuteConfig config;
    public static final String PLUGIN_NAME = "MuteManager";
    public static final String LOG_HEADER = "[" + PLUGIN_NAME + "]";
    static final Logger LOG = Logger.getLogger("Minecraft");
    private final MuteFile muteFile = new MuteFile(this);
    private final String MUTE_NOTIFY_PERM = "mutemanager.mutenotify";
    private final String UNMUTE_NOTIFY_PERM = "mutemanager.unmutenotify";
    MuteLoop muteLoop;
    MuteDatabase muteDb = null;

    @Override
    public void onEnable() {
        loadConfig(null);
        if (getMConfig().dbEnabled()) {
            muteDb = new MuteDatabase(this);
        }
        if (muteDb == null) {
            muteFile.loadMuteList();
        }
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("unmute").setExecutor(new UnMuteCommand(this));
        getCommand("mutelist").setExecutor(new MuteListCommand(this));
        getCommand("mutereload").setExecutor(new MuteReloadCommand(this));
        getServer().getPluginManager().registerEvents(mmListeners, this);
        muteLoop = new MuteLoop(this);
    }

    @Override
    public void onDisable() {
        muteLoop.end();
        if (muteDb == null) {
            muteFile.saveMuteList();
        }
        muteList.clear();
    }

    void loadConfig(CommandSender sender) {
        String loaded = "Configuration loaded.";
        String reloaded = "Configuration reloaded.";
        if (!this.configLoaded) {
            getConfig().options().copyDefaults(true);
            saveConfig();
            if (sender != null) {
                sender.sendMessage(ChatColor.GOLD + LOG_HEADER + " " + ChatColor.GRAY + loaded);
            } else {
                logInfo(loaded);
            }
            config = new MuteConfig(this);
        } else {
            reloadConfig();
            getConfig().options().copyDefaults(false);
            config = new MuteConfig(this);
            if (sender != null) {
                sender.sendMessage(ChatColor.GOLD + LOG_HEADER + " " + ChatColor.GRAY + reloaded);
            } else {
                logInfo(reloaded);
            }
        }
        configLoaded = true;
    }

    public void logInfo(String message) {
        LOG.log(Level.INFO, String.format("%s %s", LOG_HEADER, message));
    }

    public void logError(String message) {
        LOG.log(Level.SEVERE, String.format("%s %s", LOG_HEADER, message));
    }

    public void logDebug(String message) {
        if (config.debugEnabled()) {
            LOG.log(Level.INFO, String.format("%s [DEBUG] %s", LOG_HEADER, message));
        }
    }

    public MuteConfig getMConfig() {
        return config;
    }

    public void adjustMuteDuration(MutedPlayer mutedPlayer, long expTime, String reason, CommandSender sender) {
        mutedPlayer.setExptime(expTime);
        mutedPlayer.setReason(reason);
        mutedPlayer.setAuthor(sender);
        if (muteDb != null) {
            muteDb.update(mutedPlayer, expTime, reason, sender);
        }
    }

    public void addMute(MutedPlayer mutedPlayer) {
        muteList.add(mutedPlayer);
        if (muteDb != null) {
            muteDb.add(mutedPlayer);
        }
    }

    public void delMute(MutedPlayer mutedPlayer) {
        muteList.remove(mutedPlayer);
        if (muteDb != null) {
            muteDb.rem(mutedPlayer);
        }
    }

    public void delMute(String p) {
        if (muteDb != null) {
            muteDb.rem(p);
        }
    }

    public void mutePlayer(Player player, Long muteTime, CommandSender sender, String reason) {
        if (player.hasPermission("mutemanager.muteexempt")) {
            if (!config.msgExempt().isEmpty()) {
                sender.sendMessage(config.msgExempt().replace("%PLAYER%", player.getDisplayName()));
            }
            logDebug("Player " + player.getName() + " is exempt due to mutemanager.muteexempt.");
            return;
        }
        long curTime = System.currentTimeMillis();
        long expTime = curTime + (muteTime * 60 * 1000);
        MutedPlayer mutedPlayer;
        if (isMuted(player)) {
            mutedPlayer = getMutedPlayer(player);
            adjustMuteDuration(mutedPlayer, expTime, reason, sender);
        } else {
            mutedPlayer = new MutedPlayer(player, expTime, reason, sender);
            addMute(mutedPlayer);
        }
        String senderMessage = tokenize(mutedPlayer, config.msgPlayerNowMuted());
        if (config.shouldNotify()) {
            logDebug("Notifying users [" + MUTE_NOTIFY_PERM + "]: " + senderMessage);
            sendNotification(senderMessage, MUTE_NOTIFY_PERM);
        } else {
            logDebug("Notifying user [" + sender.getName() + "]: " + senderMessage);
            sender.sendMessage(senderMessage);
        }
        if (!config.msgYouHaveBeenMuted().isEmpty()) {
            player.sendMessage(tokenize(mutedPlayer, config.msgYouHaveBeenMuted()));
        }
    }

    public void mutePlayer(String player, UUID uuid, Long muteTime, CommandSender sender, String reason) {
        long curTime = System.currentTimeMillis();
        long expTime = curTime + (muteTime * 60 * 1000);
        MutedPlayer mutedPlayer;
        if (isMuted(uuid)) {
            mutedPlayer = getMutedPlayer(uuid);
            adjustMuteDuration(mutedPlayer, expTime, reason, sender);
        } else {
            mutedPlayer = new MutedPlayer(player, uuid, expTime, reason, sender);
            addMute(mutedPlayer);
        }
        addMute(mutedPlayer);
        String senderMessage = tokenize(mutedPlayer, config.msgPlayerNowMuted());
        if (config.shouldNotify()) {
            logDebug("Notifying users [" + MUTE_NOTIFY_PERM + "]: " + senderMessage);
            sendNotification(senderMessage, MUTE_NOTIFY_PERM);
        } else {
            logDebug("Notifying users [" + sender.getName() + "]: " + senderMessage);
            sender.sendMessage(senderMessage);
        }
    }

    public void unMutePlayer(String pName, CommandSender sender) {
        String senderMessage = config.msgSenderUnMuted()
                .replace("%PLAYER%", pName)
                .replace("%AUTHOR%", sender.getName());
        String playerMessage = config.msgYouHaveBeenUnMuted()
                .replace("%PLAYER%", pName)
                .replace("%AUTHOR%", sender.getName());
        boolean success = unMutePlayer(pName);
        if (success) {
            if (config.shouldNotify()) {
                logDebug("Notifying users [" + UNMUTE_NOTIFY_PERM + "]: " + senderMessage);
                sendNotification(senderMessage, UNMUTE_NOTIFY_PERM);
            } else {
                logDebug("Notifying user [" + sender.getName() + "]: " + senderMessage);
                sender.sendMessage(senderMessage);
            }
            logInfo(pName + " has been unmuted!");
            if (!playerMessage.isEmpty()) {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (player.getName().equalsIgnoreCase(pName)) {
                        player.sendMessage(playerMessage);
                        break;
                    }
                }
            }
        } else {
            sender.sendMessage(config.msgUnableToUnMute().replace("%PLAYER%", pName));
        }
    }

    public boolean unMutePlayer(String p) {
        logDebug("Unmuting: " + p);
        String pName = p;
        int idx = -1;
        for (MutedPlayer mutedPlayer : muteList) {
            if (mutedPlayer.getPlayerName().equalsIgnoreCase(pName)) {
                idx = muteList.indexOf(mutedPlayer);
                break;
            }
        }
        if (idx >= 0) {
            muteList.remove(idx);
            delMute(p);
            return true;
        }
        return false;
    }

    public boolean unMutePlayer(MutedPlayer mutedPlayer) {
        logDebug("Unmuting: " + mutedPlayer.getPlayerName());
        if (muteList.contains(mutedPlayer)) {
            delMute(mutedPlayer);
            return true;
        }
        return false;
    }

    public boolean isMuted(Player player) {
        for (MutedPlayer mutedPlayer : muteList) {
            if (mutedPlayer.getUUID().equals(player.getUniqueId())) {
                return mutedPlayer.isMuted();
            }
        }
        return false;
    }

    public boolean isMuted(OfflinePlayer player) {
        for (MutedPlayer mutedPlayer : muteList) {
            if (mutedPlayer.getUUID().equals(player.getUniqueId())) {
                return mutedPlayer.isMuted();
            }
        }
        return false;
    }

    public boolean isMuted(UUID uuid) {
        for (MutedPlayer mutedPlayer : muteList) {
            if (mutedPlayer.getUUID().equals(uuid)) {
                return mutedPlayer.isMuted();
            }
        }
        return false;
    }

    public String splitAndJoin(String[] s) {
        String joined = "";
        for (String s2 : s) {
            joined = joined + " " + s2;
        }
        return joined.substring(1).toLowerCase();
    }

    public boolean isBlockedCmd(String[] cmd) {
        String joinedCommand = splitAndJoin(cmd);
        logDebug("joinedCommand: " + joinedCommand);
        for (String s : getMConfig().blockedCmds()) {
            String joinedBlock = splitAndJoin(s.split(" "));
            logDebug("joinedBlock: " + joinedBlock);
            if (joinedCommand.equalsIgnoreCase(joinedBlock)
                    || joinedCommand.startsWith(joinedBlock + " ")) {
                return true;
            }
        }
        return false;
    }

    public MutedPlayer getMutedPlayer(Player player) {
        MutedPlayer mPlayer = null;
        for (MutedPlayer mutedPlayer : muteList) {
            if (mutedPlayer.getPlayerName().equals(player.getName())) {
                return mutedPlayer;
            }
        }
        return mPlayer;
    }

    public MutedPlayer getMutedPlayer(UUID uuid) {
        MutedPlayer mPlayer = null;
        for (MutedPlayer mutedPlayer : muteList) {
            if (mutedPlayer.getUUID().equals(uuid)) {
                return mutedPlayer;
            }
        }
        return mPlayer;
    }

    public Player lookupPlayer(String pName) {
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.getName().equals(pName) && config.reqFullName()) {
                return player;
            }
            if (player.getName().toLowerCase().startsWith(pName)) {
                return player;
            }
        }
        return null;
    }

    public String tokenize(MutedPlayer mutedPlayer, String template) {
        String duration = config.msgDuration().replace("%DURATION%", mutedPlayer.getExpiredTime(config));
        String reason = config.msgReason().replace("%REASON%", mutedPlayer.getReason());
        return template
                .replace("%DURATION%", mutedPlayer.getExpiredTime(config))
                .replace("%REASON%", mutedPlayer.getReason())
                .replace("%DURATIONTEXT%", duration)
                .replace("%REASONTEXT%", reason)
                .replace("%AUTHOR%", mutedPlayer.getAuthor())
                .replace("%PLAYER%", mutedPlayer.getPlayerName())
                .trim();
    }

    private void sendNotification(String message, String perm) {
        Bukkit.getConsoleSender().sendMessage(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            logDebug("[p: " + player.getName() + "] [" + perm + ": " + player.hasPermission(perm) + "]");
            if (player.hasPermission(perm)) {
                player.sendMessage(message);
            }
        }
    }

}
