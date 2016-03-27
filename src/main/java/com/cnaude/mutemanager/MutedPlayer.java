package com.cnaude.mutemanager;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class MutedPlayer implements Serializable {

    private final UUID uuid;
    private final String playerName;
    private Long expTime;
    private String author;
    private String reason;

    public MutedPlayer(OfflinePlayer player, Long expTime, String reason) {
        uuid = player.getUniqueId();
        playerName = player.getName();
        this.expTime = expTime;
        this.author = "";
        this.reason = reason;
    }

    public MutedPlayer(OfflinePlayer player, Long expTime, String reason, CommandSender sender) {
        uuid = player.getUniqueId();
        playerName = player.getName();
        this.expTime = expTime;
        this.author = sender.getName();
        this.reason = reason;
    }

    public MutedPlayer(String playerName, UUID uuid, Long expTime, String reason) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.expTime = expTime;
        this.author = "";
        this.reason = reason;
    }

    public MutedPlayer(String playerName, UUID uuid, Long expTime, String reason, CommandSender sender) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.expTime = expTime;
        this.author = sender.getName();
        this.reason = reason;
    }
    
    public MutedPlayer(String playerName, UUID uuid, Long expTime, String reason, String author) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.expTime = expTime;
        this.author = author;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public void setAuthor(CommandSender sender) {
        this.author = sender.getName();
    }
    
    public void setAuthor(String sender) {
        this.author = sender;
    }

    public void setExptime(long expTime) {
        this.expTime = expTime;
    }

    public boolean isMuted() {
        return expTime > System.currentTimeMillis();
    }

    public String getExpiredTime(MuteConfig config) {
        DecimalFormat formatter = new DecimalFormat("0.00");
        float diffTime = ((expTime - System.currentTimeMillis()) / 1000f) / 60f;
        if (diffTime > 5256000) {
            return config.msgForever();
        }
        if (diffTime > 525600) {
            return (formatter.format(diffTime / 525600f)) + " " + config.msgYears();
        }
        if (diffTime > 1440) {
            return (formatter.format(diffTime / 1440f)) + " " + config.msgDays();
        }
        if (diffTime > 60) {
            return (formatter.format(diffTime / 60f)) + " " + config.msgHOurs();
        }
        if (diffTime < 1f) {
            return (formatter.format(diffTime * 60f)) + " " + config.msgSeconds();
        }
        return (formatter.format(diffTime)) + " " + config.msgMinutes();
    }

    public long getExpTime() {
        return expTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getAuthor() {
        return author;
    }

    public UUID getUUID() {
        return uuid;
    }
}
