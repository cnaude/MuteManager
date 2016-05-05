package com.cnaude.mutemanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class MuteDatabase {

    private final MuteConfig config;
    private final MuteManager plugin;
    Connection conn = null;

    private final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS `mutedPlayers`"
            + "( `uuid` CHAR(36) NOT NULL , `playerName` VARCHAR(256) NOT NULL "
            + ", `expTime` DOUBLE NOT NULL , `author` VARCHAR(256) NOT NULL "
            + ", `reason` VARCHAR(2048) NOT NULL )";

    public MuteDatabase(MuteManager instance) {
        plugin = instance;
        config = plugin.getMConfig();

        connect();

    }

    private void connect() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Statement stmt;

                plugin.logInfo("Loading driver " + config.dbDriver() + "...");
                try {
                    Class.forName(config.dbDriver());
                } catch (ClassNotFoundException ex) {
                    plugin.logError(ex.getMessage());
                    return;
                }

                try {
                    plugin.logInfo("Connecting to " + config.dbUrl() + " ...");
                    conn = DriverManager.getConnection(config.dbUrl(), config.dbUser(), config.dbPass());
                } catch (SQLException ex) {
                    plugin.logError(ex.getMessage());
                    return;
                }

                try {
                    plugin.logInfo("Creating table if not exist ...");
                    stmt = conn.createStatement();
                    stmt.execute(SQL_CREATE);
                } catch (SQLException ex) {
                    plugin.logError(ex.getMessage());
                    return;
                }
                if (conn != null) {
                    loadMuteList();
                }
            }
        });
    }

    protected void loadMuteList() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                String sql = "SELECT * FROM mutedPlayers";
                Statement stmt = null;
                try {
                    stmt = conn.createStatement();
                } catch (SQLException ex) {
                    plugin.logError(ex.getMessage());
                }

                if (stmt != null) {
                    ResultSet rs = null;
                    try {
                        rs = stmt.executeQuery(sql);
                    } catch (SQLException ex) {
                        plugin.logError(ex.getMessage());
                    }
                    if (rs != null) {
                        try {
                            while (rs.next()) {
                                MutedPlayer mutedPlayer = new MutedPlayer(
                                        rs.getString("playerName"),
                                        UUID.fromString(rs.getString("uuid")),
                                        rs.getLong("expTime"),
                                        rs.getString("reason"),
                                        rs.getString("author")
                                );
                                plugin.muteList.add(mutedPlayer);
                            }
                        } catch (SQLException ex) {
                            plugin.logError(ex.getMessage());
                        }
                    }
                }
            }
        });
    }

    protected void add(final MutedPlayer mutedPlayer) {
        if (conn == null) {
            plugin.logError("Not connected!");
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                PreparedStatement preparedStatement;
                try {
                    String sql = "INSERT INTO mutedPlayers"
                            + "(uuid, playerName, expTime, author, reason) VALUES"
                            + "(?,?,?,?,?)";
                    preparedStatement = conn.prepareStatement(sql);

                    preparedStatement.setString(1, mutedPlayer.getUUID().toString());
                    preparedStatement.setString(2, mutedPlayer.getPlayerName());
                    preparedStatement.setDouble(3, mutedPlayer.getExpTime());
                    preparedStatement.setString(4, mutedPlayer.getAuthor());
                    preparedStatement.setString(5, mutedPlayer.getReason());

                    preparedStatement.execute();
                } catch (SQLException ex) {
                    plugin.logError(ex.getMessage());
                }
            }
        });
    }

    protected void rem(final MutedPlayer mutedPlayer) {
        if (conn == null) {
            plugin.logError("Not connected!");
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                PreparedStatement preparedStatement;
                try {
                    String sql = "DELETE FROM mutedPlayers where uuid = ?";
                    preparedStatement = conn.prepareStatement(sql);

                    preparedStatement.setString(1, mutedPlayer.getUUID().toString());

                    preparedStatement.execute();
                } catch (SQLException ex) {
                    plugin.logError(ex.getMessage());
                }
            }
        });
    }

    protected void rem(final String p) {
        if (conn == null) {
            plugin.logError("Not connected!");
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                PreparedStatement preparedStatement;
                try {
                    String sql = "DELETE FROM mutedPlayers where playerName = ?";
                    preparedStatement = conn.prepareStatement(sql);

                    preparedStatement.setString(1, p);

                    preparedStatement.execute();
                } catch (SQLException ex) {
                    plugin.logError(ex.getMessage());
                }
            }
        });
    }

    public void update(final MutedPlayer mutedPlayer, final long expTime, final String reason, final CommandSender sender) {
        if (conn == null) {
            plugin.logError("Not connected!");
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                PreparedStatement preparedStatement;
                try {
                    String sql = "UPDATE mutedPlayers SET expTime = ?, reason = ?, author = ? WHERE uuid = ?";
                    preparedStatement = conn.prepareStatement(sql);

                    preparedStatement.setDouble(1, expTime);
                    preparedStatement.setString(2, reason);
                    preparedStatement.setString(3, sender.getName());
                    preparedStatement.setString(4, mutedPlayer.getUUID().toString());

                    preparedStatement.execute();
                } catch (SQLException ex) {
                    plugin.logError(ex.getMessage());
                }
            }
        });
    }

}
