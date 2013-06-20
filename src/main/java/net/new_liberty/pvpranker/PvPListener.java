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
        if (!(hurt instanceof Player)) {
            return;
        }

        Player player = (Player) hurt;
        if (player.getHealth() > 0) {
            return;
        }

        Entity cause = event.getDamager();
        if (!(cause instanceof Player)) {
            return;
        }

        Player damager = (Player) cause;

        final String name = player.getName();
        final String damagerName = damager.getName();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                // TODO: Replace placeholder milestone
                PvPer causePvPer = plugin.getPvPer(damagerName);
                causePvPer.addKill("placeholder", name);
            }
        });
    }
}
