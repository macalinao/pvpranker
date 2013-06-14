package net.new_liberty.pvpranker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper class to interact with the database in an object-oriented fashion.
 */
public class PvPer {
    private final PvPRanker plugin;

    private final String name;

    public PvPer(PvPRanker plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the score of this PvPer.
     *
     * @return
     */
    public int getScore() {
        String query = "SELECT SUM(score) AS value "
                + "FROM pvpr_scores "
                + "WHERE player = ?";
        Object res = plugin.getDb().get(query, 0, name);
        if (res == null) {
            return 0;
        }
        return ((Integer) res).intValue();
    }

    /**
     * Gets this player's score from the given milestone. Case sensitive.
     *
     * @param milestone
     * @return
     */
    public int getScore(String milestone) {
        String query = "SELECT score AS value "
                + "FROM pvpr_scores "
                + "WHERE player = ? AND milestone = ?";
        Object res = plugin.getDb().get(query, 0, name, milestone);
        if (res == null) {
            return 0;
        }
        return ((Integer) res).intValue();
    }

    /**
     * Sets the player's score for a given milestone.
     *
     * @param milestone
     * @param score
     */
    public void setScore(String milestone, int score) {
        String query = "UPDATE pvpr_scores SET score = ? "
                + "WHERE player = ? AND milestone = ?";
        plugin.getDb().update(query, score, name, milestone);
    }
}
