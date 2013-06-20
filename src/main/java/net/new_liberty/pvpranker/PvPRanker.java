package net.new_liberty.pvpranker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.dbutils.ResultSetHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PvPRanker main class
 */
public class PvPRanker extends JavaPlugin {
    private static ResultSetHandler<LinkedHashMap<String, Integer>> REPORT_HANDLER = new ResultSetHandler<LinkedHashMap<String, Integer>>() {
        @Override
        public LinkedHashMap<String, Integer> handle(ResultSet rs) throws SQLException {
            LinkedHashMap<String, Integer> ret = new LinkedHashMap<String, Integer>();
            while (rs.next()) {
                ret.put(rs.getString("player"), rs.getInt("score"));
            }
            return ret;
        }
    };

    private Database db;

    private Map<String, Rank> ranks;

    private Rank lowest = null;

    @Override
    public void onEnable() {
        // Make sure the config has been made
        saveDefaultConfig();

        // Load
        loadConfig();

        // Load DB info
        String dbUser = getConfig().getString("db.user");
        String dbPass = getConfig().getString("db.pass", "");
        String dbHost = getConfig().getString("db.host");
        int dbPort = getConfig().getInt("db.port");
        String dbName = getConfig().getString("db.name");
        db = new Database(this, dbUser, dbPass, dbHost, dbPort, dbName);
        if (!db.isValid()) {
            getLogger().log(Level.SEVERE, "[PvPRanker] Could not connect to database. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Create tables
        db.update("CREATE TABLE IF NOT EXISTS pvpr_kills ("
                + "id INT(10) NOT NULL AUTO_INCREMENT,"
                + "player varchar(16) NOT NULL,"
                + "killed varchar(16) NOT NULL,"
                + "milestone varchar(255) NOT NULL,"
                + "PRIMARY KEY (id));");

        Bukkit.getPluginManager().registerEvents(new PvPListener(this), this);

        getCommand("pvp").setExecutor(new PvPCommand(this));
    }

    @Override
    public void onDisable() {
        if (!isEnabled()) {
            return;
        }
        ranks = null;
        db = null;
    }

    /**
     * Loads the config.
     */
    private void loadConfig() {
        // Load ranks
        ranks = new HashMap<String, Rank>();
        ConfigurationSection s = getConfig().getConfigurationSection("ranks");
        if (s == null) {
            s = getConfig().createSection("ranks");
        }

        for (String id : s.getKeys(false)) {
            String name = s.getString(id + ".name", id);
            int score = s.getInt(id + ".score");
            ranks.put(id, new Rank(id, name, score));
        }

        // Calculate lowest rank
        for (Rank rank : ranks.values()) {
            if (lowest == null) {
                lowest = rank;
                continue;
            }

            if (rank.getScore() < lowest.getScore()) {
                lowest = rank;
            }
        }
    }

    /**
     * Gets our database.
     *
     * @return
     */
    public Database getDb() {
        return db;
    }

    /**
     * Gets a PvPer by their name. Case sensitive.
     *
     * @param name
     * @return
     */
    public PvPer getPvPer(String name) {
        return new PvPer(this, name);
    }

    /**
     * Gets a rank by its id.
     *
     * @param id
     * @return
     */
    public Rank getRank(String id) {
        return ranks.get(id);
    }

    /**
     * Gets a rank from a score.
     *
     * @param score
     * @return
     */
    public Rank getRank(int score) {
        Rank ret = null;
        for (Rank rank : ranks.values()) {
            if (rank.getScore() > score) {
                continue; // We want ranks with a score less than this
            }

            if (ret == null) {
                ret = rank;
                continue;
            }

            if (rank.getScore() > ret.getScore()) {
                ret = rank;
            }
        }

        // Get lowest rank if necessary
        if (ret == null) {
            ret = lowest;
        }

        return ret;
    }

    /**
     * Generates a score report based on all-time stats.
     *
     * @param limit The number of records to get.
     *
     * @return A score report with the highest scores first. Keys are the player
     * and values are the player's score.
     */
    public LinkedHashMap<String, Integer> generateScoreReport(int limit) {
        String query = "SELECT player, COUNT(id) AS score FROM pvpr_kills GROUP BY player ORDER BY score DESC LIMIT ?";
        return db.query(query, REPORT_HANDLER, limit);
    }

    /**
     * Generates a score report.
     *
     * @param limit The number of records to get.
     * @param milestone The milestone of the report.
     *
     * @return A score report with the highest scores first. Keys are the player
     * and values are the player's score.
     */
    public LinkedHashMap<String, Integer> generateScoreReport(int limit, String milestone) {
        if (milestone == null) {
            return generateScoreReport(limit);
        }

        String query = "SELECT player, milestone, COUNT(id) AS score FROM pvpr_kills WHERE milestone = ? GROUP BY player, milestone ORDER BY score DESC LIMIT ?";
        return db.query(query, REPORT_HANDLER, milestone, limit);
    }
}
