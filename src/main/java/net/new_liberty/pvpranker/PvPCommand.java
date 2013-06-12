package net.new_liberty.pvpranker;

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

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Your score is " + plugin.getPvPer(sender.getName()).getScore());
        }
        return true;
    }
}
