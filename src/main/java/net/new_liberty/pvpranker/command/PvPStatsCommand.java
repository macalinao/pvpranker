package net.new_liberty.pvpranker.command;

import net.new_liberty.pvpranker.PvPRanker;
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Your score is " + plugin.getPvPer(sender.getName()).getScore());
        return true;
    }
}
