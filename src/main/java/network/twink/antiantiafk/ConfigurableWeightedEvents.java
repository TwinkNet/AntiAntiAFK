package network.twink.antiantiafk;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurableWeightedEvents {

    public static final String EVENTS_PREFIX = "events.";
    public static final String onNewChunkGeneratedYamlVal = "new-chunk-generated"; // inherently unique
    public static final String oldChunkLoadedYamlVal = "old-chunk-loaded"; // only a unique chunk for this session
    public static final String onBlockPlaceYamlVal = "block-placed"; // only in a unique Location
    public static final String onBlockBreakYamlVal = "block-broken"; // only in a unique Location
    public static final String onPortalYamlVal = "dimension-changed"; // only in a unique Location
    public static final String onContainerOpenYamlVal = "container-opened"; // only in a unique Location
    public static final String onElytraEngageYamlVal = "elytra-used"; // only in a unique Location
    public static final String onDeathYamlVal = "player-died"; // only in a unique Location
    public static final String onChatYamlVal = "player-chatted";
    public static final String onInteractYamlVal = "player-right-click"; // only in a unique Location

    private float onNewChunkGenerated; // inherently unique
    private float oldChunkLoaded; // only a unique chunk for this session
    private float onBlockPlace; // only in a unique Location
    private float onBlockBreak; // only in a unique Location
    private float onPortal; // only in a unique Location
    private float onContainerOpen; // only in a unique Location
    private float onElytraEngage; // only in a unique Location
    private float onDeath; // only in a unique Location
    private float onChat;
    private float onInteract; // only in a unique Location

    private final FileConfiguration config;


    public ConfigurableWeightedEvents(AntiAntiAfkPlugin plugin) {
        plugin.saveResource("config.yml", false);
        config = plugin.getConfig();
        this.onNewChunkGenerated = (float) config.getDouble(EVENTS_PREFIX + onNewChunkGeneratedYamlVal);
        this.oldChunkLoaded = (float) config.getDouble(EVENTS_PREFIX + oldChunkLoadedYamlVal);
        this.onBlockPlace = (float) config.getDouble(EVENTS_PREFIX + onBlockPlaceYamlVal);
        this.onBlockBreak = (float) config.getDouble(EVENTS_PREFIX + onBlockBreakYamlVal);
        this.onPortal = (float) config.getDouble(EVENTS_PREFIX + onPortalYamlVal);
        this.onContainerOpen = (float) config.getDouble(EVENTS_PREFIX + onContainerOpenYamlVal);
        this.onElytraEngage = (float) config.getDouble(EVENTS_PREFIX + onElytraEngageYamlVal);
        this.onDeath = (float) config.getDouble(EVENTS_PREFIX + onDeathYamlVal);
        this.onChat = (float) config.getDouble(EVENTS_PREFIX + onChatYamlVal);
        this.onInteract = (float) config.getDouble(EVENTS_PREFIX + onInteractYamlVal);
    }

    public float getOnNewChunkGenerated() {
        return onNewChunkGenerated;
    }

    public float getOldChunkLoaded() {
        return oldChunkLoaded;
    }

    public float getOnBlockPlace() {
        return onBlockPlace;
    }

    public float getOnBlockBreak() {
        return onBlockBreak;
    }

    public float getOnPortal() {
        return onPortal;
    }

    public float getOnContainerOpen() {
        return onContainerOpen;
    }

    public float getOnElytraEngage() {
        return onElytraEngage;
    }

    public float getOnDeath() {
        return onDeath;
    }

    public float getOnChat() {
        return onChat;
    }

    public float getOnInteract() {
        return onInteract;
    }

}
