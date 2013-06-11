package net.new_liberty.pvpranker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PvPRanker main class
 */
public class PvPRanker extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PvPListener(this), this);
    }
}
