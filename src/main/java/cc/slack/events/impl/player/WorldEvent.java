package cc.slack.events.impl.player;

import cc.slack.events.Event;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldEvent extends Event {

    private final WorldClient changeWorld;

    /**
     * Constructs a new WorldEvent object with the specified world client.
     *
     * @param worldClient The world client associated with the event.
     */
    public WorldEvent(WorldClient worldClient) {
        this.changeWorld = worldClient;
    }

    /**
     * Retrieves the world client associated with the event.
     *
     * @return The world client associated with the event.
     */
    public WorldClient getChangeWorld() {
        return changeWorld;
    }
}
