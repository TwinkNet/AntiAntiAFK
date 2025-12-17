package network.twink.antiantiafk;

public class ConfigurableWeightedEvents {

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


    public ConfigurableWeightedEvents(AntiAntiAfkPlugin plugin) {
        // init config.yml
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
