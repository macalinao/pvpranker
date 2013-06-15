package net.new_liberty.pvpranker;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * PvPRanker listener
 */
public class PvPListener implements Listener {
    private final PvPRanker plugin;

    public PvPListener(PvPRanker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity hurt = event.getEntity();
        if (hurt instanceof Player) {
            Player player = (Player) hurt;
            if (player.getHealth() <= 0) {
                Entity cause = event.getDamager();
                if (cause instanceof Player) {
                    Player damager = (Player) cause;

                    final PvPer hurtPvPer = plugin.getPvPer(player.getName());
                    final PvPer causePvPer = plugin.getPvPer(damager.getName());
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                        @Override
                        public void run() {
                            // TODO: Replace placeholders
                            hurtPvPer.setScore("placeholder", hurtPvPer.getScore() - 1);
                            causePvPer.setScore("placeholder", causePvPer.getScore() + 1);
                        }
                    });
                }
            }
        }
    }
}
