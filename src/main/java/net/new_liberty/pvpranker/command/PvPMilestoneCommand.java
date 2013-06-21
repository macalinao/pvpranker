package net.new_liberty.pvpranker.command;

import net.new_liberty.pvpranker.PvPRanker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Sets the milestone.
 */
public class PvPMilestoneCommand implements CommandExecutor {
    private final PvPRanker plugin;

    public PvPMilestoneCommand(PvPRanker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            return false;
        }

        plugin.setMilestone(args[0]);
        sender.sendMessage(ChatColor.YELLOW + "Milestone set to " + ChatColor.GREEN + args[0] + ChatColor.YELLOW + ".");
        return true;
    }
}
