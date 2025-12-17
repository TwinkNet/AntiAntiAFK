package network.twink.antiantiafk;

import io.papermc.paper.math.BlockPosition;
import org.bukkit.event.Listener;

import java.util.List;

public class AFKPlayer implements Listener {

    private final Thread timerDecreaseThread;
    private float secondsBeforeDisconnect;
    private List<BlockPosition> blockPositions;

    public AFKPlayer(float initialSeconds) {
        this.secondsBeforeDisconnect = initialSeconds;
        Runnable timer = () -> {
            while(true) {
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

    public float addSecondsBeforeDisconnect(float additionalSeconds) {
        this.secondsBeforeDisconnect += additionalSeconds;
        return secondsBeforeDisconnect;
    }

    public float getSecondsBeforeDisconnect() {
        return secondsBeforeDisconnect;
    }
}
