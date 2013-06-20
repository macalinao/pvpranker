package net.new_liberty.pvpranker;

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
        String query = "SELECT COUNT(id) AS value "
                + "FROM pvpr_kills "
                + "WHERE player = ?";
        Object res = plugin.getDb().get(query, 0, name);
        if (res == null) {
            return 0;
        }
        return ((Long) res).intValue();
    }

    /**
     * Gets this player's score from the given milestone. Case sensitive.
     *
     * @param milestone
     * @return
     */
    public int getScore(String milestone) {
        String query = "SELECT COUNT(id) AS value "
                + "FROM pvpr_kills "
                + "WHERE player = ? AND milestone = ?";
        Object res = plugin.getDb().get(query, 0, name, milestone);
        if (res == null) {
            return 0;
        }
        return ((Long) res).intValue();
    }

    /**
     * Adds a kill for the player.
     *
     * @param killed The player killed.
     * @param score
     */
    public void addKill(String killed, String milestone) {
        String query = "INSERT INTO pvpr_kills (player, killed, milestone) "
                + "VALUES (?, ?, ?)";
        plugin.getDb().update(query, name, killed, milestone);
    }
}
