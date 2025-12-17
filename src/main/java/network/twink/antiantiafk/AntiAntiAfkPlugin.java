package network.twink.antiantiafk;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public final class AntiAntiAfkPlugin extends JavaPlugin implements Listener {

    public static final String BYPASS_PERMISSION = "antiantiafk.bypass";

    public Logger logger;
    private Map<UUID, AFKPlayer> uuidafkPlayerMap;

    @Override
    public void onEnable() {
        logger = this.getLogger();
        logger.info("Anti-AntiAFK is initialising");
        uuidafkPlayerMap = new HashMap<>();
        for (Player onlinePlayer : this.getServer().getOnlinePlayers()) {
            uuidafkPlayerMap.put(onlinePlayer.getUniqueId(), new AFKPlayer(1800f)); // add current players, needed if server is reloaded with people online.
        }
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    private AFKPlayer getAfkPlayer(Player player) {
        return uuidafkPlayerMap.get(player.getUniqueId());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        AFKPlayer afkPlayer = new AFKPlayer(1800f);
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

    @EventHandler
    public void onChunkGen(PlayerChunkLoadEvent e) {
        AFKPlayer afkPlayer = getAfkPlayer(e.getPlayer());
        if (afkPlayer == null) {
            logger.warning("Anti-AntiAFK is unexpectedly unable to monitor " + e.getPlayer().getName() + ".");
            return;
        }
        if (e.getPlayer().hasPermission("antiantiafk.bypass")) {
            return;
        }
        if (!e.getChunk().isGenerated()) {
            // new chunk

        }
    }

    @EventHandler
    public void onPlayerBlockBreak(PlayerHarvestBlockEvent event) {
        event.getHarvestedBlock().getLocation();
    }
}
