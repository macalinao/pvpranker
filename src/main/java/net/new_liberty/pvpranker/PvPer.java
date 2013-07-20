package net.new_liberty.pvpranker;

import com.massivecraft.factions.FPlayers;
import com.simplyian.easydb.EasyDB;
import java.util.Map;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Wrapper class to interact with the database in an object-oriented fashion.
 *
 * Use this class in a different thread so the main thread isn't blocked with
 * database calls.
 */
public class PvPer {
    private static final MapHandler MAP_HANDLER = new MapHandler();

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
        return ((Number) EasyDB.getDb().get(query, 0, name)).intValue();
    }

    /**
     * Gets the score of this PvPer at the given milestone.
     *
     * @param milestone
     * @return
     */
    public int getScore(String milestone) {
        String query = "SELECT score "
                + "FROM pvpr_scores "
                + "WHERE player = ? AND milestone = ?";
        return ((Number) EasyDB.getDb().get(query, 0, name, milestone)).intValue();
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
     * Gets this player's stats in a Map.
     *
     * <ul>
     * <li>player - The player</li>
     * <li>score - The score</li>
     * <li>kills - Player's kills</li>
     * <li>deaths - Player's deaths</li>
     * <li>most_killed - Player they have the most kills on</li>
     * <li>most_killed_count - Number of kills on that player</li>
     * <li>most_killed_by - Player they have been killed by most</li>
     * <li>most_killed_by_count - Number of kills by that player</li>
     * </ul>
     *
     * @param milestone
     * @return
     */
    public Map<String, Object> getStats(String milestone) {
        String query = "SELECT"
                + "	A.player, A.score, B.kills, C.deaths, D.killed AS most_killed, D.kills AS most_killed_count, E.player AS most_killed_by, E.kills AS most_killed_by_count"
                + "FROM"
                + "(SELECT player, score FROM pvpr_scores WHERE player = ? AND milestone = ?) AS A"
                + "LEFT OUTER JOIN (SELECT COUNT(*) AS kills FROM pvpr_kills WHERE player = ? AND milestone = ?) AS B ON TRUE"
                + "LEFT OUTER JOIN (SELECT COUNT(*) AS deaths FROM pvpr_kills WHERE killed = ? AND milestone = ?) AS C ON TRUE"
                + "LEFT OUTER JOIN (SELECT killed, COUNT(*) AS kills FROM pvpr_kills WHERE player = ? AND milestone = ? GROUP BY killed ORDER BY kills DESC LIMIT 1) AS D ON TRUE"
                + "LEFT OUTER JOIN (SELECT player, COUNT(*) AS kills FROM pvpr_kills WHERE killed = ? AND milestone = ? GROUP BY player ORDER BY kills DESC LIMIT 1) AS E ON TRUE";

        return EasyDB.getDb().query(query, MAP_HANDLER, name, milestone, name, milestone, name, milestone, name, milestone, name, milestone);
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
        return ((Number) EasyDB.getDb().get(query, 0, name)).intValue();
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
        return ((Number) EasyDB.getDb().get(query, 0, name, milestone)).intValue();
    }

    /**
     * Gets the number of times this PvPer has killed a player in the past day.
     *
     * @param player
     * @return
     */
    public int getDayKillCount(String killed) {
        String query = "SELECT COUNT(id) AS value FROM pvpr_kills WHERE player = ? AND killed = ? AND time > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY)";
        return ((Number) EasyDB.getDb().get(query, 0, name, killed)).intValue();
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
        String playerFaction = ChatColor.stripColor(FPlayers.i.get(name).getFaction().getTag());
        String otherFaction = ChatColor.stripColor(FPlayers.i.get(killed).getFaction().getTag());

        EasyDB.getDb().update(query, name, killed, playerFaction, otherFaction, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), milestone);
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
        EasyDB.getDb().update(query, name, milestone, amount, amount);
    }
}
