package net.new_liberty.pvpranker;

import com.simplyian.easydb.Database;
import com.simplyian.easydb.EasyDB;
import net.new_liberty.pvpranker.command.PvPStatsCommand;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import net.new_liberty.pvpranker.command.PvPMilestoneCommand;
import org.apache.commons.dbutils.ResultSetHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PvPRanker main class
 */
public class PvPRanker extends JavaPlugin {

    private String milestone;

    @Override
    public void onEnable() {
        // Make sure the config has been made
        saveDefaultConfig();

        // Load
        loadConfig();

        PvPListener listener = new PvPListener(this);

        // Hook into PEX
        if (!listener.setupChat()) {
            getLogger().log(Level.SEVERE, "Could not set up hook into permissions plugin for groups. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Load DB
        Database db = EasyDB.getDb();
        if (!db.isValid()) {
            getLogger().log(Level.SEVERE, "Could not connect to database. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Create tables

        // Holds kill counts
        db.update("CREATE TABLE IF NOT EXISTS pvpr_kills ("
                + "id INT(10) NOT NULL AUTO_INCREMENT,"
                + "player varchar(16) NOT NULL,"
                + "killed varchar(16) NOT NULL,"
                + "player_faction varchar(32) NOT NULL,"
                + "killed_faction varchar(32) NOT NULL,"
                + "world varchar(32) NOT NULL,"
                + "x INT(10) NOT NULL,"
                + "y INT(10) NOT NULL,"
                + "z INT(10) NOT NULL,"
                + "milestone varchar(255) NOT NULL,"
                + "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (id));");

        Bukkit.getPluginManager().registerEvents(listener, this);

        getCommand("pvpmilestone").setExecutor(new PvPMilestoneCommand(this));

        getLogger().log(Level.INFO, "Plugin enabled.");
    }

    /**
     * Loads the config.
     */
    private void loadConfig() {
        milestone = getConfig().getString("milestone", "default");
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
     * Gets the milestone the server is currently on.
     *
     * @return
     */
    public String getMilestone() {
        return milestone;
    }

    /**
     * Sets the milestone of the server.
     */
    public void setMilestone(String milestone) {
        this.milestone = milestone;
        getConfig().set("milestone", milestone);
        saveConfig();
    }

}
