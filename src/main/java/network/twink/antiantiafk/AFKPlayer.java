package network.twink.antiantiafk;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AFKPlayer implements Listener {

    private AntiAntiAfkPlugin plugin;
    private final Thread timerDecreaseThread;
    private float secondsBeforeDisconnect;
    private List<BlockPos> blockPositions;
    private List<Long> chunkKeys;

    public AFKPlayer(AntiAntiAfkPlugin plugin, float initialSeconds) {
        this.plugin = plugin;
        if (this.plugin == null) {
            throw new IllegalArgumentException("Unexpected value passed for AntiAntiAfkPlugin plugin: cannot be null");
        }
        this.blockPositions = new ArrayList<>();
        this.chunkKeys = new ArrayList<>();
        this.secondsBeforeDisconnect = initialSeconds;
        Runnable timer = () -> {
            while(getSecondsBeforeDisconnect() > 0) {
                secondsBeforeDisconnect -= 1f;
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        };
        this.timerDecreaseThread = new Thread(timer);
    }

    @EventHandler
    public void onChunkGen(PlayerChunkLoadEvent e) {
        if (e.getPlayer().hasPermission("antiantiafk.bypass")) {
            return;
        }
        if (!e.getChunk().isGenerated()) {
            addSecondsBeforeDisconnect(plugin.getWeightedEvents().getOnNewChunkGenerated());
            chunkKeys.add(e.getChunk().getChunkKey());
        } else if (!chunkKeys.contains(e.getChunk().getChunkKey())) {
            addSecondsBeforeDisconnect(plugin.getWeightedEvents().getOldChunkLoaded());
            chunkKeys.add(e.getChunk().getChunkKey());
        } else {
            // do we do anything? to be determined...
            return;
        }
    }

    @EventHandler
    public void onPlayerBlockBreak(PlayerHarvestBlockEvent event) {
        Location loc = event.getHarvestedBlock().getLocation();
        BlockPos pos = new BlockPos(loc);
        if (!blockPositions.contains(pos)) {
            blockPositions.add(new BlockPos(loc));
            addSecondsBeforeDisconnect(this.plugin.getWeightedEvents().getOnBlockBreak());
        }
        // we are not going to give the player more time for placing a block in the same location that they've already done something in.
    }
    @EventHandler
    public void onPlayerBlockBreak(BlockPlaceEvent event) {
        Location loc = event.getBlockPlaced().getLocation();
        BlockPos pos = new BlockPos(loc);
        if (!blockPositions.contains(pos)) {
            blockPositions.add(new BlockPos(loc));
            addSecondsBeforeDisconnect(this.plugin.getWeightedEvents().getOnBlockPlace());
        }
        // we are not going to give the player more time for placing a block in the same location that they've already done something in.
    }
    @EventHandler
    public void onPlayerContainerOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof Container container) {
            BlockPos pos = new BlockPos(container.getLocation());
            if (!blockPositions.contains(pos)) {
                addSecondsBeforeDisconnect(this.plugin.getWeightedEvents().getOnContainerOpen());
            }
            // do we do anything? to be determined... containers don't normally move around often
        }
    }

    public float addSecondsBeforeDisconnect(float additionalSeconds) {
        this.secondsBeforeDisconnect += additionalSeconds;
        return secondsBeforeDisconnect;
    }

    public float getSecondsBeforeDisconnect() {
        return secondsBeforeDisconnect;
    }
}
