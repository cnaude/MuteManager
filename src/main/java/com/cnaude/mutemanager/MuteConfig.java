package com.cnaude.mutemanager;

import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

/**
 *
 * @author cnaude
 */
public final class MuteConfig {

    private final Configuration config;
    private final MuteManager plugin;

    private static final String GLOBAL_NOTIFY = "Global.Notify";
    private static final String DEFAULT_TIME = "Global.DefaultTime";
    private static final String MAX_TIME = "Global.MaxTime";
    private static final String DEFAULT_REASON = "Global.DefaultReason";
    private static final String ADMIN_LISTEN = "Global.AdminListen";
    private static final String BROADCAST_NODE = "Global.BroadcastNode";
    private static final String COMMANDS = "Global.Commands";
    private static final String BLOCK_CMDS = "Global.BlockCommands";
    private static final String ALLOW_OFFLINE_MUTE = "Global.AllowOfflineMute";
    private static final String REQUIRE_FULL_NAME = "Global.RequireFullName";
    private static final String GLOBAL_DEBUG = "Global.Debug";

    private static final String MSG_NO_PERM = "Messages.NoPermission";
    private static final String MSG_ZERO_SECS = "Messages.ZeroSeconds";
    private static final String MSG_SECONDS = "Messages.Seconds";
    private static final String MSG_MINUTES = "Messages.Minutes";
    private static final String MSG_HOURS = "Messages.Hours";
    private static final String MSG_DAYS = "Messages.Days";
    private static final String MSG_YEARS = "Messages.Years";
    private static final String MSG_FOREVER = "Messages.Forever";
    private static final String MSG_UNABLE_TO_UNMUTE = "Messages.UnableToUnMute";
    private static final String MSG_YOU_HAVE_BEEN_UNMUTED = "Messages.YouHaveBeenUnMuted";
    private static final String MSG_YOU_HAVE_BEEN_MUTED = "Messages.YouHaveBeenMuted";
    private static final String MSG_P_NOW_MUTED = "Messages.PlayerNowMuted";
    private static final String MSG_P_NOW_UNMUTED = "Messages.PlayerNowUnMuted";
    private static final String MSG_NO_PLAYER = "Messages.NoPlayer";
    private static final String MSG_REASON = "Messages.Reason";
    private static final String MSG_DURATION = "Messages.Duration";
    private static final String MSG_YOU_ARE_MUTED = "Messages.YouAreMuted";
    private static final String MSG_MAX_REASON = "Messages.MaxReason";
    private static final String MSG_EXEMPT = "Messages.Exempt";
    
    private static final String DB_ENABLED = "Database.Enabled";
    private static final String DB_USER = "Database.User";
    private static final String DB_PASS = "Database.Pass";
    private static final String DB_URL = "Database.Url";
    private static final String DB_DRIVER = "Database.Driver";    

    private boolean shouldNotify;
    private long defaultTime;
    private long maxTime;
    private String defaultReason;
    private boolean adminListen;
    private String broadcastNode;
    private List<String> blockedCommands = Arrays.asList();
    private boolean allowOfflineMute;
    private boolean reqFullName;

    private String msgNoPerm;
    private String msgZeroSeconds;
    private String msgSeconds;
    private String msgMinutes;
    private String msgHours;
    private String msgDays;
    private String msgYears;
    private String msgForever;
    private String msgUnableToUnMute;
    private String msgYouHaveBeenUnMuted;
    private String msgYouHaveBeenMuted;
    private String msgPlayerUnMuted;
    private String msgNoPlayer;
    private String msgPlayerNowMuted;
    private String msgReason;
    private String msgDuration;
    private String msgYouAreMuted;
    private String msgMaxReason;
    private String msgExempt;
    
    private String dbUser;
    private String dbPass;
    private String dbUrl;
    private String dbDriver;
    private boolean dbEnabled;

    private boolean debugEnabled;

    public MuteConfig(MuteManager instance) {
        plugin = instance;
        config = this.plugin.getConfig();
        loadValues();
    }

    public void loadValues() {
        debugEnabled = config.getBoolean(GLOBAL_DEBUG, false);

        shouldNotify = config.getBoolean(GLOBAL_NOTIFY, true);
        defaultTime = config.getInt(DEFAULT_TIME, 5);
        maxTime = config.getInt(MAX_TIME, 52594900);
        defaultReason = config.getString(DEFAULT_REASON, "None");
        adminListen = config.getBoolean(ADMIN_LISTEN, false);
        broadcastNode = config.getString(BROADCAST_NODE, "mutemanager.listen");
        reqFullName = config.getBoolean(REQUIRE_FULL_NAME, false);
        if (config.getBoolean(BLOCK_CMDS, false)) {
            blockedCommands = config.getStringList(COMMANDS);
        }
        allowOfflineMute = config.getBoolean(ALLOW_OFFLINE_MUTE, false);

        msgNoPerm = config.getString(MSG_NO_PERM);
        msgZeroSeconds = config.getString(MSG_ZERO_SECS);
        msgSeconds = config.getString(MSG_SECONDS);
        msgMinutes = config.getString(MSG_MINUTES);
        msgHours = config.getString(MSG_HOURS);
        msgDays = config.getString(MSG_DAYS);
        msgYears = config.getString(MSG_YEARS);
        msgForever = config.getString(MSG_FOREVER);
        msgUnableToUnMute = config.getString(MSG_UNABLE_TO_UNMUTE);
        msgYouHaveBeenUnMuted = config.getString(MSG_YOU_HAVE_BEEN_UNMUTED);
        msgYouHaveBeenMuted = config.getString(MSG_YOU_HAVE_BEEN_MUTED);
        msgPlayerNowMuted = config.getString(MSG_P_NOW_MUTED);
        msgPlayerUnMuted = config.getString(MSG_P_NOW_UNMUTED);
        msgNoPlayer = config.getString(MSG_NO_PLAYER);
        msgReason = config.getString(MSG_REASON);
        msgDuration = config.getString(MSG_DURATION);
        msgYouAreMuted = config.getString(MSG_YOU_ARE_MUTED);
        msgMaxReason = config.getString(MSG_MAX_REASON);
        msgExempt = config.getString(MSG_EXEMPT);
        
        dbUser = config.getString(DB_USER);
        dbPass = config.getString(DB_PASS);
        dbUrl = config.getString(DB_URL);
        dbDriver = config.getString(DB_DRIVER);
        dbEnabled = config.getBoolean(DB_ENABLED);

    }

    public boolean shouldNotify() {
        return shouldNotify;
    }

    public Long defaultTime() {
        return defaultTime;
    }

    public Long maxTime() {
        return maxTime;
    }

    public String defaultReason() {
        return defaultReason;
    }

    public boolean adminListen() {
        return adminListen;
    }

    public String broadcastNode() {
        return broadcastNode;
    }

    public List<String> blockedCmds() {
        return blockedCommands;
    }

    public boolean allowOfflineMute() {
        return allowOfflineMute;
    }

    public boolean reqFullName() {
        return reqFullName;
    }

    public String msgNoPerm() {
        return ChatColor.translateAlternateColorCodes('&', msgNoPerm);
    }

    public String msgZeroSeconds() {
        return ChatColor.translateAlternateColorCodes('&', msgZeroSeconds);
    }

    public String msgSeconds() {
        return ChatColor.translateAlternateColorCodes('&', msgSeconds);
    }

    public String msgMinutes() {
        return ChatColor.translateAlternateColorCodes('&', msgMinutes);
    }

    public String msgHOurs() {
        return ChatColor.translateAlternateColorCodes('&', msgHours);
    }

    public String msgDays() {
        return ChatColor.translateAlternateColorCodes('&', msgDays);
    }

    public String msgYears() {
        return ChatColor.translateAlternateColorCodes('&', msgYears);
    }

    public String msgForever() {
        return ChatColor.translateAlternateColorCodes('&', msgForever);
    }

    public String msgUnableToUnMute() {
        return ChatColor.translateAlternateColorCodes('&', msgUnableToUnMute);
    }

    public String msgYouHaveBeenUnMuted() {
        return ChatColor.translateAlternateColorCodes('&', msgYouHaveBeenUnMuted);
    }

    public String msgYouHaveBeenMuted() {
        return ChatColor.translateAlternateColorCodes('&', msgYouHaveBeenMuted);
    }

    public String msgSenderUnMuted() {
        return ChatColor.translateAlternateColorCodes('&', msgPlayerUnMuted);
    }

    public String msgNoPlayer() {
        return ChatColor.translateAlternateColorCodes('&', msgNoPlayer);
    }

    public String msgPlayerNowMuted() {
        return ChatColor.translateAlternateColorCodes(('&'), msgPlayerNowMuted);
    }

    public String msgReason() {
        return ChatColor.translateAlternateColorCodes(('&'), msgReason);
    }

    public String msgDuration() {
        return ChatColor.translateAlternateColorCodes(('&'), msgDuration);
    }

    public String msgYouAreMuted() {
        return ChatColor.translateAlternateColorCodes(('&'), msgYouAreMuted);
    }

    public String msgMaxReason(long muteTime) {
        return ChatColor.translateAlternateColorCodes(('&'), msgMaxReason
                .replace("%MAX%", String.valueOf(maxTime))
                .replace("%TIME%", String.valueOf(muteTime)));
    }
    
    public String msgExempt() {
        return ChatColor.translateAlternateColorCodes(('&'), msgExempt);
    }
    
    protected String dbUser() {
        return dbUser;
    }
    
    protected String dbPass() {
        return dbPass;
    }
    
    protected String dbUrl() {
        return dbUrl;
    }
    
    protected String dbDriver() {
        return dbDriver;
    }
    
    protected boolean dbEnabled() {
        return dbEnabled;
    }

    public boolean debugEnabled() {
        return debugEnabled;
    }
}
