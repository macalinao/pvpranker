package net.new_liberty.pvpranker;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database stuff. MySQL sucks.
 */
public final class Database {
    private final PvPRanker plugin;

    private final String user;

    private final String pass;

    private final String url;

    private Connection connection = null;

    public Database(PvPRanker plugin, String user, String pass, String host, String port, String database) {
        this.plugin = plugin;
        this.user = user;
        this.pass = pass;
        url = "jdbc:mysql://" + host + ":" + port + "/" + database;
    }

    /**
     * Connects to the database.
     *
     * @return True if the database connected successfully.
     */
    public boolean connect() {
        // Check if already connecteds
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    return true;
                }
            } catch (SQLException ex) {
            }
        }

        try {
            connection = DriverManager.getConnection(url, user, pass);
            return true;
        } catch (SQLException ex) {
        }

        return false;
    }

    /**
     * Disposes of the database connection.
     */
    public void dispose() {
        try {
            connection.close();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "[PvPRanker] Could not close DB connection!", ex);
        }
        connection = null;
    }

    /**
     * Executes a database update.
     *
     * @param query
     */
    public void update(String query) {
        connect();

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "[PvPRanker] Could not run database query '" + query + "'!", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "[PvPRanker] Could not run close statement for query '" + query + "'!", ex);
                }
            }
        }
    }

    /**
     * Executes a query.
     *
     * @param query
     * @return
     */
    public ResultSet execute(String query) {
        connect();

        Statement statement = null;
        ResultSet ret = null;
        try {
            statement = connection.createStatement();
            ret = statement.executeQuery(query);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "[PvPRanker] Could not run database query '" + query + "'!", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "[PvPRanker] Could not run close statement for query '" + query + "'!", ex);
                }
            }
        }
        return ret;
    }
}
