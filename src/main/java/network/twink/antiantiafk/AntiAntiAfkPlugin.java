package network.twink.antiantiafk;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public final class AntiAntiAfkPlugin extends JavaPlugin implements Listener {

    public static final String BYPASS_PERMISSION = "antiantiafk.bypass";

    public Logger logger;
    private Map<UUID, AFKPlayer> uuidafkPlayerMap;
    private ConfigurableWeightedEvents weightedEvents;

    @Override
    public void onEnable() {
        logger = this.getLogger();
        logger.info("Anti-AntiAFK is initialising");
        weightedEvents = new ConfigurableWeightedEvents(this);
        uuidafkPlayerMap = new HashMap<>();
        for (Player onlinePlayer : this.getServer().getOnlinePlayers()) {
            uuidafkPlayerMap.put(onlinePlayer.getUniqueId(), new AFKPlayer(this, 1800f)); // add current players, needed if server is reloaded with people online.
        }
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    /* pkg-priv */ AFKPlayer getAfkPlayer(Player player) {
        return uuidafkPlayerMap.get(player.getUniqueId());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        AFKPlayer afkPlayer = new AFKPlayer(this, 1800f);
        uuidafkPlayerMap.put(e.getPlayer().getUniqueId(), afkPlayer);
        this.getServer().getPluginManager().registerEvents(afkPlayer, this);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        var afkPlayer = getAfkPlayer(e.getPlayer());
        // All events used in AFKPlayer are going to have to be listed here.
        // This is a bit annoying, and I'm not seeing a better way to do this without doing hacky crap.
        PlayerChunkLoadEvent.getHandlerList().unregister(afkPlayer);
    }

    public ConfigurableWeightedEvents getWeightedEvents() {
        return this.weightedEvents;
    }
}
