package net.new_liberty.pvpranker.command;

import java.text.NumberFormat;
import java.util.Map;
import java.util.Map.Entry;
import net.new_liberty.pvpranker.PvPRanker;
import net.new_liberty.pvpranker.PvPer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * PvP Command
 */
public class PvPStatsCommand implements CommandExecutor {
    private final PvPRanker plugin;

    public PvPStatsCommand(PvPRanker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(ChatColor.RED + "You must choose a player to view their stats.");
            return true;
        }

        String nameStr = sender.getName();
        if (args.length > 0) {
            nameStr = args[0];
        }

        final String name = nameStr;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                PvPer pvper = plugin.getPvPer(name);

                sender.sendMessage(ChatColor.YELLOW + "=== " + name + "'s PvP Stats ===");

                Map<String, Object> stats = pvper.getStats(plugin.getMilestone());
                if (stats == null) {
                    sender.sendMessage(ChatColor.YELLOW + "This player hasn't been involved in any kills yet.");
                    return;
                }
                Object scoreObj = stats.get("score");
                int score = 0;
                if (scoreObj != null) {
                    score = ((Number) scoreObj).intValue();
                }

                Object killsObj = stats.get("kills");
                int kills = 0;
                if (killsObj != null) {
                    kills = ((Number) killsObj).intValue();
                }

                Object deathsObj = stats.get("deaths");
                int deaths = 0;
                if (deathsObj != null) {
                    deaths = ((Number) deathsObj).intValue();
                }

                String kdr = NumberFormat.getNumberInstance().format((double) kills / deaths);

                sender.sendMessage(ChatColor.GREEN + "Rank: " + plugin.getRank(score).getName());
                sender.sendMessage(
                        ChatColor.GREEN + "Score: " + ChatColor.YELLOW + score + "    "
                        + ChatColor.GREEN + "Kills: " + ChatColor.YELLOW + kills + "    "
                        + ChatColor.GREEN + "Deaths: " + ChatColor.YELLOW + deaths + "    "
                        + ChatColor.GREEN + "KDR: " + ChatColor.YELLOW + kdr);

                Object mostKilled = stats.get("most_killed");
                String mostKilledMsg;
                if (mostKilled != null) {
                    mostKilledMsg = mostKilled + " (" + stats.get("most_killed_count") + " times)";
                } else {
                    mostKilledMsg = "This player hasn't killed anyone yet.";
                }

                sender.sendMessage(ChatColor.GREEN + "Most killed: " + ChatColor.YELLOW + mostKilledMsg);

                Object mostKilledBy = stats.get("most_killed_by");
                String mostKilledByMsg;
                if (mostKilledBy != null) {
                    mostKilledByMsg = mostKilledBy + " (" + stats.get("most_killed_by_count") + " times)";
                } else {
                    mostKilledByMsg = "This player hasn't killed anyone yet.";
                }

                sender.sendMessage(ChatColor.GREEN + "Most killed by: " + ChatColor.YELLOW + mostKilledByMsg);
            }
        });

        return true;
    }
}
