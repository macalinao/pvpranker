package net.new_liberty.pvpranker;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PvPRanker main class
 */
public class PvPRanker extends JavaPlugin {
    private Map<String, Rank> ranks;

    @Override
    public void onEnable() {
        // Make sure the config has been made
        saveDefaultConfig();

        // Load
        loadRanks();

        Bukkit.getPluginManager().registerEvents(new PvPListener(this), this);
    }

    @Override
    public void onDisable() {
        ranks = null;
    }

    /**
     * Loads the ranks.
     */
    private void loadRanks() {
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
        return ret;
    }
}
