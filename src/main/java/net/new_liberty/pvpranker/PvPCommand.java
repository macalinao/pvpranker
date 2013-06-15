package net.new_liberty.pvpranker;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * PvP Command
 */
public class PvPCommand implements CommandExecutor {
    private final PvPRanker plugin;

    public PvPCommand(PvPRanker plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Your score is " + plugin.getPvPer(sender.getName()).getScore());
        } else {
            if (args[0].equalsIgnoreCase("top")) {
                int limit = 5;
                if (args.length > 1) {
                    try {
                        limit = Integer.parseInt(args[1]);
                        if (limit > 10) {
                            sender.sendMessage("Limit cannot be over 10!");
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Usage: /pvp top [limit]");
                        return true;
                    }
                }

                final int theLimit = limit;
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        sender.sendMessage("PvP Top Scores");
                        LinkedHashMap<String, Integer> map = plugin.generateScoreReport(theLimit);
                        for (Entry<String, Integer> playerScore : map.entrySet()) {
                            sender.sendMessage(playerScore.getKey() + " - " + playerScore.getValue());
                        }
                    }
                });
            } else {
                sender.sendMessage("Unrecognised subcommand of /pvp!");
            }
        }
        return true;
    }
}
