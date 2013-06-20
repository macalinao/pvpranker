package net.new_liberty.pvpranker;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * PvP Command
 */
public class PvPCommand implements CommandExecutor {
    private final PvPRanker plugin;

    public PvPCommand(PvPRanker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Your score is " + plugin.getPvPer(sender.getName()).getScore());
        } else {
            if (args[0].equalsIgnoreCase("top")) {
                top(sender, args);
            } else {
                sender.sendMessage("Unrecognised subcommand of /pvp!");
            }
        }
        return true;
    }

    private void top(final CommandSender sender, String[] args) {
        int limit = 5;
        if (args.length > 1) {
            try {
                limit = Integer.parseInt(args[1]);
                if (limit > 10) {
                    sender.sendMessage("Limit cannot be over 10!");
                    return;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Usage: /pvp top [limit]");
                return;
            }
        }

        final int theLimit = limit;

        // http://i.qkme.me/3ux37g.jpg I'm so funny
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                final LinkedHashMap<String, Integer> map = plugin.generateScoreReport(theLimit);
                Bukkit.getServer().getScheduler().callSyncMethod(plugin, new Callable<Object>() {
                    @Override
                    public Object call() {
                        sender.sendMessage("PvP Top Scores");
                        for (Entry<String, Integer> playerScore : map.entrySet()) {
                            sender.sendMessage(playerScore.getKey() + " - " + playerScore.getValue());
                        }
                        return null;
                    }
                });
            }
        });
    }
}
