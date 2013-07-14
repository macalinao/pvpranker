package net.new_liberty.pvpranker;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * PvPRanker listener
 */
public class PvPListener implements Listener {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, yyyy hh:mm aaa");

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

        String fprefix = "";
        FPlayer player = FPlayers.i.get(p);
        if (FPlayers.i.get(p).hasFaction()) {
            fprefix = Conf.chatTagReplaceString;
        }
        fprefix = "[" + fprefix + "]";

        String rank = ChatColor.WHITE + "{" + plugin.getPvPer(p.getName()).getRank(plugin.getMilestone()).getName() + ChatColor.WHITE + "}";
        if (p.hasPermission("pvpranker.hiderank")) {
            rank = "";
        }
        String prefix = ChatColor.translateAlternateColorCodes('&', chat.getPlayerPrefix(p));
        String suffix = ChatColor.translateAlternateColorCodes('&', chat.getPlayerSuffix(p));

        String format = fprefix + rank + prefix + " " + p.getName() + ChatColor.WHITE + ": " + suffix + "%2$s";
        event.setFormat(format);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!(player.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }

        Entity cause = ((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager();
        Player killer;

        if (cause instanceof Player) {
            killer = (Player) cause;

        } else if (cause instanceof Projectile) {
            Projectile proj = (Projectile) cause;
            Entity shooter = proj.getShooter();
            if (!(shooter instanceof Player)) {
                return;
            }
            killer = (Player) shooter;

        } else {
            return;
        }


        Location loc = player.getLocation();

        // Skull handling
        double chance = plugin.getConfig().getDouble("head-drop-chance", 0.1);
        if (chance > Math.random()) {
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            SkullMeta sm = (SkullMeta) skull.getItemMeta();
            sm.setOwner(player.getName());
            sm.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.WHITE + "Killed by " + ChatColor.AQUA + killer.getName() + ChatColor.WHITE + " on " + ChatColor.YELLOW + DATE_FORMAT.format(new Date())));
            skull.setItemMeta(sm);
            loc.getWorld().dropItemNaturally(loc, skull);
        }

        (new KillUpdateTask(plugin, loc, killer.getName(), player.getName())).runTaskAsynchronously(plugin);
    }
}
