package net.new_liberty.pvpranker.command;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.new_liberty.pvpranker.PvPRanker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author simplyianm
 */
public class PvPTopCommand implements CommandExecutor {
    private final PvPRanker plugin;

    public PvPTopCommand(PvPRanker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        String milestone = null;
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
                    sender.sendMessage(ChatColor.GREEN + "Nobody " + (theMilestone == null ? "has killed anyone yet" : "killed anyone during " + theMilestone) + ".");

                } else {
                    int i = 1;
                    for (Entry<String, Integer> playerScore : map.entrySet()) {
                        sender.sendMessage(ChatColor.GREEN + Integer.toString(i++) + ") " + playerScore.getKey() + " - " + playerScore.getValue());
                    }
                }
            }
        });

        return true;
    }
}
