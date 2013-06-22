package net.new_liberty.pvpranker;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * PvPRanker listener
 */
public class PvPListener implements Listener {
    private final PvPRanker plugin;

    public PvPListener(PvPRanker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity victim = event.getEntity();
        if (!(victim instanceof Player)) {
            return;
        }

        Player player = (Player) victim;
        if (player.getHealth() > 0) {
            return;
        }

        if (!(victim.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }

        Entity cause = ((EntityDamageByEntityEvent) victim.getLastDamageCause()).getDamager();
        if (!(cause instanceof Player)) {
            return;
        }

        Player damager = (Player) cause;

        final String killedName = player.getName();
        final String killerName = damager.getName();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                // TODO: Replace placeholder milestone
                PvPer killer = plugin.getPvPer(killerName);
                PvPer killed = plugin.getPvPer(killedName);

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
                        rp.sendMessage(ChatColor.RED + "Unfortunately, you have been demoted to " + killedRank.getName() + ChatColor.RED + ".");
                    }
                }

                killer.addKill(plugin.getMilestone(), killedName);
            }
        });
    }
}
