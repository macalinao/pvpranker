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
        PreparedStatement ps = null;
        String query = "SELECT SUM(score) AS score "
                + "FROM pvpr_scores "
                + "WHERE player = ?";

        int ret = 0;
        try {
            ps = plugin.getDb().prepareStatement(query);
            ps.setString(1, name);
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                ret = set.getInt("score");
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not get score from ResultSet!", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not close score statement!", ex);
            }
        }

        return ret;
    }
}
