package net.new_liberty.pvpranker.command;

import net.new_liberty.pvpranker.PvPRanker;
import net.new_liberty.pvpranker.PvPer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

                if (pvper.getKillCount(plugin.getMilestone()) == 1) {
                    sender.sendMessage(ChatColor.GREEN + name + " has no PvP history since the last milestone.");
                    return;
                }

                int score = pvper.getScore(plugin.getMilestone());
                sender.sendMessage(ChatColor.GREEN + "Rank: " + plugin.getRank(score).getName());
                sender.sendMessage(ChatColor.GREEN + "Score: " + score);
            }
        });

        return true;
    }
}
