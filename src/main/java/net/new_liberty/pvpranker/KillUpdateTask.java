package net.new_liberty.pvpranker;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task to run when a player is killed.
 */
public class KillUpdateTask extends BukkitRunnable {

    private final PvPRanker plugin;

    private final Location loc;

    private final String killerName;

    private final String killedName;

    public KillUpdateTask(PvPRanker plugin, Location loc, String killerName, String killedName) {
        this.plugin = plugin;
        this.loc = loc;
        this.killerName = killerName;
        this.killedName = killedName;
    }

    @Override
    public void run() {
        PvPer killer = plugin.getPvPer(killerName);

        // Save kill
        killer.addKill(killedName, loc, plugin.getMilestone());
    }

}
