package net.new_liberty.pvpranker;

import com.massivecraft.factions.FPlayers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Wrapper class to interact with the database in an object-oriented fashion.
 *
 * Use this class in a different thread so the main thread isn't blocked with
 * database calls.
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
     * Gets the player this PvPer represents.
     *
     * @return Returns null if the player is not logged in.
     */
    public Player getPlayer() {
        return Bukkit.getPlayerExact(name);
    }

    /**
     * Gets the score of this PvPer.
     *
     * @return
     */
    public int getScore() {
        String query = "SELECT SUM(score) AS score "
                + "FROM pvpr_scores "
                + "WHERE player = ?";
        return ((Number) plugin.getDb().get(query, 0, name)).intValue();
    }

    /**
     * Gets the score of this PvPer at the given milestone.
     *
     * @param milestone
     * @return
     */
    public int getScore(String milestone) {
        String query = "SELECT SUM(score) AS score "
                + "FROM pvpr_scores "
                + "WHERE player = ? AND milestone = ?";
        return ((Number) plugin.getDb().get(query, 0, name, milestone)).intValue();
    }

    /**
     * Gets this player's rank.
     *
     * @return
     */
    public Rank getRank() {
        return plugin.getRank(getScore());
    }

    /**
     * Gets this player's rank at the given milestone.
     *
     * @param milestone
     * @return
     */
    public Rank getRank(String milestone) {
        return plugin.getRank(getScore(milestone));
    }

    /**
     * Gets the kill count of this PvPer.
     *
     * @return
     */
    public int getKillCount() {
        String query = "SELECT COUNT(id) AS value "
                + "FROM pvpr_kills "
                + "WHERE player = ?";
        return ((Number) plugin.getDb().get(query, 0, name)).intValue();
    }

    /**
     * Gets this player's kill count from the given milestone. Case sensitive.
     *
     * @param milestone
     * @return
     */
    public int getKillCount(String milestone) {
        String query = "SELECT COUNT(id) AS value "
                + "FROM pvpr_kills "
                + "WHERE player = ? AND milestone = ?";
        return ((Number) plugin.getDb().get(query, 0, name, milestone)).intValue();
    }

    /**
     * Gets the number of times this PvPer has killed a player in the past day.
     *
     * @param player
     * @return
     */
    public int getDayKillCount(String killed) {
        String query = "SELEC COUNT(id) AS value FROM pvpr_kills WHERE player = ? AND killed = ? AND date > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY)";
        return ((Number) plugin.getDb().get(query, 0, name, killed)).intValue();
    }

    /**
     * Adds a kill for the player.
     *
     * @param killed The player killed.
     * @param loc The location of the kill
     * @param milestone
     */
    public void addKill(String killed, Location loc, String milestone) {
        String query = "INSERT INTO pvpr_kills (player, killed, player_faction, killed_faction, world, x, y, z, milestone) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Let's hope this code works async
        String playerFaction = FPlayers.i.get(name).getFaction().getTag();
        String otherFaction = FPlayers.i.get(killed).getFaction().getTag();

        plugin.getDb().update(query, name, killed, playerFaction, otherFaction, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), milestone);
    }

    /**
     * Adds a specified amount to the player's score for the given milestone.
     *
     * @param amount
     * @param milestone
     */
    public void addScore(int amount, String milestone) {
        String query = "INSERT INTO pvpr_scores (player, milestone, score) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE score = score + ?";
        plugin.getDb().update(query, name, milestone, amount, amount);
    }
}
