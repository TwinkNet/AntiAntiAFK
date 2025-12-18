package network.twink.antiantiafk;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import network.twink.antiantiafk.util.BlockPos;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.*;

public class AFKPlayer implements Listener {

    private final UUID playerUuid;
    private final AntiAntiAfkPlugin plugin;
    private final Thread timerDecreaseThread;
    private float secondsBeforeDisconnect;
    private final ArrayDeque<BlockPos> blockPositions;
    private final ArrayDeque<Long> chunkKeys;

    public AFKPlayer(AntiAntiAfkPlugin plugin, UUID playerUuid, float initialSeconds) {
        this.plugin = plugin;
        this.playerUuid = playerUuid;
        if (this.plugin == null) {
            throw new IllegalArgumentException("Unexpected value passed for AntiAntiAfkPlugin plugin: cannot be null");
        }
        if (this.playerUuid == null) {
            throw new IllegalArgumentException("Unexpected value passed for UUID playerUuid: cannot be null");
        }
        this.blockPositions = new ArrayDeque<>();
        this.chunkKeys = new ArrayDeque<>();
        this.secondsBeforeDisconnect = initialSeconds;
        Runnable timer = () -> {
            while (getSecondsBeforeDisconnect() > 0) {
                if (this.getBukkitPlayer() == null || !this.getBukkitPlayer().isOnline()) return;
                secondsBeforeDisconnect -= 1f;
                if (plugin.getWeightedEventConfig().isDebug()) {
                    getBukkitPlayer().sendPlayerListHeaderAndFooter(Component.text("\n\2474Anti-AntiAFK Debug\n"),
                            Component.text("\n\2477" + AntiAntiAfkPlugin.getFormattedTime(getSecondsBeforeDisconnect()) + "\n"));
                }
                try {
                    synchronized (this) {
                        wait(1000);
                    }
                } catch (InterruptedException e) {
                    this.plugin.getLogger().info(playerUuid + "'s timerDecreaseThread was interrupted. Maybe they are being kicked for inactivity? Maybe the server is shutting down or being reloaded?");
                }
            }
            kickPlayer();
        };
        this.timerDecreaseThread = new Thread(timer);
        this.timerDecreaseThread.start();
    }

    public Player getBukkitPlayer() {
        return this.plugin.getServer().getPlayer(playerUuid);
    }

    public void interruptThread() {
        if (!this.timerDecreaseThread.isInterrupted()) this.timerDecreaseThread.interrupt();
    }

    public void kickPlayer() {
        this.interruptThread();
        if (this.getBukkitPlayer() == null || !this.getBukkitPlayer().isOnline()) return; // they're already gone.
        this.getBukkitPlayer().kick(Component.text(plugin.getWeightedEventConfig().getKickMessage())); // todo kickmsg
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChunkGen(PlayerChunkLoadEvent e) {
        if (!e.getPlayer().getUniqueId().equals(playerUuid)) return;
        if (e.getPlayer().hasPermission("antiantiafk.bypass")) {
            return;
        }
        if (!e.getChunk().isGenerated()) {
            addSecondsBeforeDisconnect(plugin.getWeightedEventConfig().getOnNewChunkGenerated());
            enqueueChunkKey(e.getChunk().getChunkKey(), this.plugin.getWeightedEventConfig().getChunkKeyQueueLength());
        } else if (!chunkKeys.contains(e.getChunk().getChunkKey())) {
            addSecondsBeforeDisconnect(plugin.getWeightedEventConfig().getOldChunkLoaded());
            enqueueChunkKey(e.getChunk().getChunkKey(), this.plugin.getWeightedEventConfig().getChunkKeyQueueLength());
        } else {
            // we don't want to punish a live player for doing repetitive things, like going back and forth between two structures in their base.
            // this may need tuning.
            addSecondsBeforeDisconnect(plugin.getWeightedEventConfig().getSeenChunkLoaded());
            enqueueChunkKey(e.getChunk().getChunkKey(), this.plugin.getWeightedEventConfig().getChunkKeyQueueLength());
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBlockBreak(PlayerHarvestBlockEvent e) {
        if (!e.getPlayer().getUniqueId().equals(playerUuid)) return;
        Location loc = e.getHarvestedBlock().getLocation();
        BlockPos pos = new BlockPos(loc);
        if (!blockPositions.contains(pos)) {
            addSecondsBeforeDisconnect(this.plugin.getWeightedEventConfig().getOnBlockBreak());
            enqueueBlockPos(pos, this.plugin.getWeightedEventConfig().getBlockPosQueueLength());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBlockPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().getUniqueId().equals(playerUuid)) return;
        Location loc = e.getBlockPlaced().getLocation();
        BlockPos pos = new BlockPos(loc);
        if (!blockPositions.contains(pos)) {
            addSecondsBeforeDisconnect(this.plugin.getWeightedEventConfig().getOnBlockPlace());
            enqueueBlockPos(pos, this.plugin.getWeightedEventConfig().getBlockPosQueueLength());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerContainerOpen(InventoryOpenEvent e) {
        if (!e.getPlayer().getUniqueId().equals(playerUuid)) return;
        if (e.getInventory().getHolder() instanceof Container container) {
            BlockPos pos = new BlockPos(container.getLocation());
            if (!blockPositions.contains(pos)) {
                addSecondsBeforeDisconnect(this.plugin.getWeightedEventConfig().getOnContainerOpen());
                enqueueBlockPos(pos, this.plugin.getWeightedEventConfig().getBlockPosQueueLength());
            }
            // do we do anything? to be determined... containers don't normally move around often
            // maybe we should give the player some time under certain circumstances.
            // still thinking about how this will work.
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) {
        if (!e.getPlayer().getUniqueId().equals(playerUuid)) return;
        BlockPos pos = new BlockPos(e.getPlayer().getLocation());
        if (!blockPositions.contains(pos)) {
            addSecondsBeforeDisconnect(this.plugin.getWeightedEventConfig().getOnDeath());
            enqueueBlockPos(pos, this.plugin.getWeightedEventConfig().getBlockPosQueueLength());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFly(EntityToggleGlideEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (!player.getUniqueId().equals(playerUuid)) return;
            BlockPos pos = new BlockPos(player.getLocation());
            if (!blockPositions.contains(pos)) {
                addSecondsBeforeDisconnect(this.plugin.getWeightedEventConfig().getOnElytraEngage());
                enqueueBlockPos(pos, this.plugin.getWeightedEventConfig().getBlockPosQueueLength());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFlyFaster(PlayerElytraBoostEvent e) {
        if (!e.getPlayer().getUniqueId().equals(playerUuid)) return;
        addSecondsBeforeDisconnect(this.plugin.getWeightedEventConfig().getOnElytraBoost());
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncChatEvent e) {
        if (!e.getPlayer().getUniqueId().equals(playerUuid)) return;
        addSecondsBeforeDisconnect(this.plugin.getWeightedEventConfig().getOnChat());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPortal(PlayerPortalEvent e) {
        if (!e.getPlayer().getUniqueId().equals(playerUuid)) return;
        BlockPos pos = new BlockPos(e.getPlayer().getLocation());
        if (!blockPositions.contains(pos)) {
            addSecondsBeforeDisconnect(this.plugin.getWeightedEventConfig().getOnPortal());
            enqueueBlockPos(pos, this.plugin.getWeightedEventConfig().getBlockPosQueueLength());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onXp(PlayerPickupExperienceEvent e) {
        if (!e.getPlayer().getUniqueId().equals(playerUuid)) return;
        for (ExperienceOrb.SpawnReason acceptableExperienceSource : AntiAntiAfkPlugin.ACCEPTABLE_EXPERIENCE_SOURCES) {
            if (acceptableExperienceSource == e.getExperienceOrb().getSpawnReason()) {
                addSecondsBeforeDisconnect(e.getExperienceOrb().getExperience() * this.plugin.getWeightedEventConfig().getOnOrganicExperienceEarned());
                break;
            }
        }
    }

    public float addSecondsBeforeDisconnect(float additionalSeconds) {
        this.secondsBeforeDisconnect += additionalSeconds;
        this.secondsBeforeDisconnect = Math.min(this.secondsBeforeDisconnect, this.plugin.getWeightedEventConfig().getMaximumSeconds());
        return secondsBeforeDisconnect;
    }

    public float getSecondsBeforeDisconnect() {
        return secondsBeforeDisconnect;
    }

    private void enqueueChunkKey(long chunkKey, int maxEntries) {
        boolean full = this.chunkKeys.size() >= maxEntries;
        chunkKeys.offer(chunkKey);
        if (full) chunkKeys.poll();
    }

    private void enqueueBlockPos(BlockPos pos, int maxEntries) {
        boolean full = this.blockPositions.size() >= maxEntries;
        blockPositions.offer(pos);
        if (full) blockPositions.poll();
    }
}
