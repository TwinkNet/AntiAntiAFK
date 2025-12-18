package network.twink.antiantiafk;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurableWeightedEvents {

    public static final String EVENTS_PREFIX = "events.";
    public static final String onNewChunkGeneratedYamlVal = "new-chunk-generated"; // inherently unique
    public static final String oldChunkLoadedYamlVal = "old-chunk-loaded"; // only a unique chunk for this session
    public static final String seenChunkLoadedYamlVal = "seen-chunk-loaded"; // only a unique chunk for this session
    public static final String onBlockPlaceYamlVal = "block-placed"; // only in a unique Location
    public static final String onBlockBreakYamlVal = "block-broken"; // only in a unique Location
    public static final String onPortalYamlVal = "dimension-changed"; // only in a unique Location
    public static final String onContainerOpenYamlVal = "container-opened"; // only in a unique Location
    public static final String onElytraEngageYamlVal = "elytra-used"; // only in a unique Location
    public static final String onElytraBoostYamlVal = "elytra-boosted";
    public static final String onDeathYamlVal = "player-died"; // only in a unique Location
    public static final String onChatYamlVal = "player-chatted";
    public static final String onInteractYamlVal = "player-right-click"; // only in a unique Location
    public static final String blockPosQueueLengthYamlVal = "blockpos-max-entries"; // only in a unique Location
    public static final String chunkKeyQueueLengthYamlVal = "chunkkey-max-entries"; // only in a unique Location
    public static final String onOrganicExperienceEarnedYamlVal = "organic-experience-earned"; // only in a unique Location


    private String bypassPermission;
    private String kickMessage;
    private int initialSeconds;
    private int maximumSeconds;
    private boolean debug;


    private float onNewChunkGenerated; // inherently unique
    private float oldChunkLoaded; // only a unique chunk for this session
    private float seenChunkLoaded;
    private float onBlockPlace; // only in a unique Location
    private float onBlockBreak; // only in a unique Location
    private float onPortal; // only in a unique Location
    private float onContainerOpen; // only in a unique Location
    private float onElytraEngage; // only in a unique Location
    private float onElytraBoost;
    private float onDeath; // only in a unique Location
    private float onChat;
    private float onInteract; // only in a unique Location
    private float onOrganicExperienceEarned; // should help alleviate some timer decay while mining with slow tools, smelting, or villager farming.
    private int blockPosQueueLength; // how many positions should be stored before the oldest position becomes "unique" again?
    private int chunkKeyQueueLength; // how many chunkkeys should be stored before the oldest chunkkey becomes "unique" again?

    private final FileConfiguration config;


    public ConfigurableWeightedEvents(AntiAntiAfkPlugin plugin) {
        plugin.saveResource("config.yml", false);
        config = plugin.getConfig();

        this.bypassPermission = config.getString("bypass-permission");
        this.kickMessage = config.getString("kick-message");
        this.initialSeconds = config.getInt("initial-seconds");
        this.maximumSeconds = config.getInt("maximum-seconds");
        this.debug = config.getBoolean("debug");

        this.onNewChunkGenerated = (float) config.getDouble(EVENTS_PREFIX + onNewChunkGeneratedYamlVal); //
        this.oldChunkLoaded = (float) config.getDouble(EVENTS_PREFIX + oldChunkLoadedYamlVal); //
        this.seenChunkLoaded = (float) config.getDouble(EVENTS_PREFIX + seenChunkLoadedYamlVal); //
        this.onBlockPlace = (float) config.getDouble(EVENTS_PREFIX + onBlockPlaceYamlVal); //
        this.onBlockBreak = (float) config.getDouble(EVENTS_PREFIX + onBlockBreakYamlVal); //
        this.onPortal = (float) config.getDouble(EVENTS_PREFIX + onPortalYamlVal); //
        this.onContainerOpen = (float) config.getDouble(EVENTS_PREFIX + onContainerOpenYamlVal); //
        this.onElytraEngage = (float) config.getDouble(EVENTS_PREFIX + onElytraEngageYamlVal); //
        this.onElytraBoost = (float) config.getDouble(EVENTS_PREFIX + onElytraBoostYamlVal); //
        this.onDeath = (float) config.getDouble(EVENTS_PREFIX + onDeathYamlVal); //
        this.onChat = (float) config.getDouble(EVENTS_PREFIX + onChatYamlVal); //
        this.onInteract = (float) config.getDouble(EVENTS_PREFIX + onInteractYamlVal); //
        this.onOrganicExperienceEarned = (float) config.getDouble(EVENTS_PREFIX + onOrganicExperienceEarnedYamlVal); //
        this.blockPosQueueLength = config.getInt(EVENTS_PREFIX + blockPosQueueLengthYamlVal);
        this.chunkKeyQueueLength = config.getInt(EVENTS_PREFIX + chunkKeyQueueLengthYamlVal);
    }

    public boolean isDebug() {
        return debug;
    }

    public int getInitialSeconds() {
        return initialSeconds;
    }

    public int getMaximumSeconds() {
        return maximumSeconds;
    }

    public String getBypassPermission() {
        return bypassPermission;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public float getOnNewChunkGenerated() {
        return onNewChunkGenerated;
    }

    public float getOldChunkLoaded() {
        return oldChunkLoaded;
    }

    public float getSeenChunkLoaded() {
        return seenChunkLoaded;
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

    public float getOnElytraBoost() {
        return onElytraBoost;
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

    public float getOnOrganicExperienceEarned() {
        return onOrganicExperienceEarned;
    }

    public int getBlockPosQueueLength() {
        return blockPosQueueLength;
    }

    public int getChunkKeyQueueLength() {
        return chunkKeyQueueLength;
    }
}
