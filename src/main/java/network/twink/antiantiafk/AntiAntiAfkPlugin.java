package network.twink.antiantiafk;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public final class AntiAntiAfkPlugin extends JavaPlugin implements Listener {

    public Logger logger;
    private Map<UUID, AFKPlayer> uuidafkPlayerMap;
    private ConfigurableWeightedEvents weightedEvents;

    public static final ExperienceOrb.SpawnReason[] ACCEPTABLE_EXPERIENCE_SOURCES = new ExperienceOrb.SpawnReason[]{ExperienceOrb.SpawnReason.VILLAGER_TRADE, ExperienceOrb.SpawnReason.BLOCK_BREAK, ExperienceOrb.SpawnReason.GRINDSTONE, ExperienceOrb.SpawnReason.FURNACE};

    @Override
    public void onEnable() {
        logger = this.getLogger();
        logger.info("Anti-AntiAFK is initialising");
        weightedEvents = new ConfigurableWeightedEvents(this);
        logger.info("Anti-AntiAFK has loaded its configuration values");
        uuidafkPlayerMap = new HashMap<>();
        for (Player onlinePlayer : this.getServer().getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(getWeightedEventConfig().getBypassPermission())) continue;
            uuidafkPlayerMap.put(onlinePlayer.getUniqueId(), new AFKPlayer(this, onlinePlayer.getUniqueId(), 1800f)); // add current players, needed if server is reloaded with people online.
            logger.info(onlinePlayer.getName() + " was already online, but they will be treated as if they just joined. A new AFKPlayer instance was created for them.");
        }
        this.getServer().getPluginManager().registerEvents(this, this);
        logger.info("Anti-AntiAFK is ready and listening for events.");
    }

    /* pkg-priv */ AFKPlayer getAfkPlayer(Player player) {
        return uuidafkPlayerMap.get(player.getUniqueId());
    }

    @Override
    public void onDisable() {
        logger.info("Shutting down Anti-AntiAFK");
        uuidafkPlayerMap.forEach(((uuid, afkPlayer) -> {
            afkPlayer.interruptThread();
            logger.info("Interrupting the timer thread for AFKPlayer " + uuid.toString());
        }));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission(getWeightedEventConfig().getBypassPermission())) return;
        AFKPlayer afkPlayer = new AFKPlayer(this, e.getPlayer().getUniqueId(), 1800f);
        uuidafkPlayerMap.put(e.getPlayer().getUniqueId(), afkPlayer);
        this.getServer().getPluginManager().registerEvents(afkPlayer, this);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        var afkPlayer = getAfkPlayer(e.getPlayer());
        if (afkPlayer == null) return;
        afkPlayer.interruptThread();
        // All events used in AFKPlayer are going to have to be listed here.
        // This is a bit annoying, and I'm not seeing a better way to do this without doing hacky crap.
        PlayerChunkLoadEvent.getHandlerList().unregister(afkPlayer);
        PlayerHarvestBlockEvent.getHandlerList().unregister(afkPlayer);
        BlockPlaceEvent.getHandlerList().unregister(afkPlayer);
        InventoryOpenEvent.getHandlerList().unregister(afkPlayer);
        PlayerDeathEvent.getHandlerList().unregister(afkPlayer);
        EntityToggleGlideEvent.getHandlerList().unregister(afkPlayer);
        PlayerElytraBoostEvent.getHandlerList().unregister(afkPlayer);
        AsyncChatEvent.getHandlerList().unregister(afkPlayer);
    }

    public static String getFormattedTime(float seconds) {
        int roundedDown = (int) seconds;
        StringBuilder builder = new StringBuilder();
        int hours = 0;
        int minutes = 0;
        int sec = 0;
        for (; roundedDown > 3600; roundedDown -= 3600) {
            hours++;
        }
        for (; roundedDown > 60; roundedDown -= 60) {
            minutes++;
        }
        sec = roundedDown;
        builder.append(hours).append("h ").append(minutes).append("m ").append(sec).append("s");
        return builder.toString();
    }

    public ConfigurableWeightedEvents getWeightedEventConfig() {
        return this.weightedEvents;
    }
}
