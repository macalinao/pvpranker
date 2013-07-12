package net.new_liberty.pvpranker;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
        PvPer killed = plugin.getPvPer(killedName);

        // Save kill
        killer.addKill(killedName, loc, plugin.getMilestone());

        int count = killer.getDayKillCount(killedName);

        // Check if there were too many kills
        int max = plugin.getConfig().getInt("max-kills-per-day", 10);
        if (count >= max) {
            Player rp = killer.getPlayer();
            if (rp != null) {
                rp.sendMessage(ChatColor.RED + "You have already killed " + killedName + " at least " + max + " times today, so you do not receive any points.");
            }

            Player dp = killed.getPlayer();
            if (dp != null) {
                dp.sendMessage(ChatColor.YELLOW + "You have not lost points for this death due to " + killerName + " already killing you at least " + max + " times today.");
            }
            return;
        }

        // Score
        int killerScore = killer.getScore(plugin.getMilestone());
        int killedScore = killed.getScore(plugin.getMilestone());

        // Calculate worth
        Rank oldKilledRank = plugin.getRank(killedScore); // Initial rank
        int worth = oldKilledRank.getWorth();
        int newKilledScore = killedScore - worth;
        if (killedScore < plugin.getLowestRank().getScore()) {
            worth = plugin.getLowestRank().getScore() - killedScore;
            if (worth < 0) {
                worth = 0;
            }
            newKilledScore = killedScore - worth;
        }

        // If worth is 0, let's save some query time
        if (worth == 0) {
            Player rp = killer.getPlayer();
            if (rp != null) {
                rp.sendMessage(ChatColor.YELLOW + "You didn't gain any points from this kill because the person you killed has died too much.");
            }

            Player dp = killed.getPlayer();
            if (dp != null) {
                dp.sendMessage(ChatColor.YELLOW + "Good news: you didn't lose any points. Bad news: you're still a " + oldKilledRank.getName() + ChatColor.YELLOW + ".");
            }
            return;
        }

        // If worth is not 0, let's transfer worth appropriately
        killer.addScore(worth, plugin.getMilestone());
        killed.addScore(-worth, plugin.getMilestone());

        Player rp = killer.getPlayer();
        if (rp != null) {
            rp.sendMessage(ChatColor.GREEN + "You have received " + worth + " points from killing " + killedName + ".");

            int newKillerScore = killerScore + worth;
            Rank oldKillerRank = plugin.getRank(killerScore);
            Rank killerRank = plugin.getRank(newKillerScore);

            if (!oldKillerRank.equals(killerRank)) {
                rp.sendMessage(ChatColor.GREEN + "Congratulations, you have been promoted to " + killerRank.getName() + ChatColor.GREEN + "!");
            }
        }

        Player dp = killed.getPlayer();
        if (dp != null) {
            dp.sendMessage(ChatColor.RED + "You have lost " + worth + " points from being killed by " + killerName + ".");

            Rank killedRank = plugin.getRank(newKilledScore);

            if (!oldKilledRank.equals(killedRank)) {
                dp.sendMessage(ChatColor.RED + "Unfortunately, you have been demoted to " + killedRank.getName() + ChatColor.RED + ".");
            }
        }

    }
}
