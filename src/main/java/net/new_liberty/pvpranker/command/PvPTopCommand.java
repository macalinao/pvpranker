package net.new_liberty.pvpranker.command;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import net.new_liberty.pvpranker.PvPRanker;
import net.new_liberty.pvpranker.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Displays the top PvPers on the server.
 */
public class PvPTopCommand implements CommandExecutor {
    private final PvPRanker plugin;

    public PvPTopCommand(PvPRanker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        String milestone = plugin.getMilestone();
        if (args.length > 0) {
            milestone = args[0];
        }

        final String theMilestone = milestone;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                final LinkedHashMap<String, Integer> map = plugin.generateScoreReport(10, theMilestone);
                sender.sendMessage(ChatColor.YELLOW + "== PvP Top Scores ==");
                if (map.isEmpty()) {
                    sender.sendMessage(ChatColor.GREEN + "Nobody " + (theMilestone.equals(plugin.getMilestone()) ? "has killed anyone yet" : "killed anyone during " + theMilestone) + ".");

                } else {
                    int i = 1;
                    for (Entry<String, Integer> playerScore : map.entrySet()) {
                        int score = playerScore.getValue().intValue();
                        Rank rank = plugin.getRank(score);
                        sender.sendMessage(ChatColor.GREEN + Integer.toString(i++) + ") " + playerScore.getKey() + ": " + score + " " + rank.getName());
                    }
                }
            }
        });

        return true;
    }
}
