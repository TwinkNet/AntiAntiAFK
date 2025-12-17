package network.twink.antiantiafk;

public class WeightedEvents {

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


    public WeightedEvents(AntiAntiAfkPlugin plugin) {
        // init config
    }

}
