package net.new_liberty.pvpranker;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * PvPRanker listener
 */
public class PvPListener implements Listener {
    private static class FPlayer {
        public FPlayer() {
        }
    }
    private final PvPRanker plugin;

    private Chat chat;

    public PvPListener(PvPRanker plugin) {
        this.plugin = plugin;
    }

    public boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return (chat != null);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();

        String fprefix = "[{factions_relcolor}" + ChatColor.BOLD + "{factions_roleprefix}"
                + ChatColor.RESET + "{factions_relcolor}{factions_name}" + ChatColor.WHITE + "]";
        String rank = ChatColor.WHITE + "{" + plugin.getPvPer(p.getName()).getRank(plugin.getMilestone()).getName() + ChatColor.WHITE + "}";
        if (p.hasPermission("pvpranker.hiderank")) {
            rank = "";
        }
        String prefix = ChatColor.translateAlternateColorCodes('&', chat.getPlayerPrefix(p));
        String suffix = ChatColor.translateAlternateColorCodes('&', chat.getPlayerSuffix(p));

        String format = fprefix + rank + prefix + " " + p.getName() + ": " + suffix + "%2$s";
        event.setFormat(format);
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

        Player killer = (Player) cause;


        final Location loc = player.getLocation();

        // Skull handling
        double chance = plugin.getConfig().getDouble("head-drop-chance", 0.1);
        if (chance > Math.random()) {
            ItemStack skull = new ItemStack(Material.SKULL_ITEM);
            SkullMeta sm = (SkullMeta) skull.getItemMeta();
            sm.setOwner(player.getName());
            skull.setItemMeta(sm);
            loc.getWorld().dropItemNaturally(loc, skull);
        }

        // Our closure
        final String killedName = player.getName();
        final String killerName = killer.getName();

        (new KillUpdateTask(plugin, loc, killerName, killedName)).runTaskAsynchronously(plugin);
    }
}
