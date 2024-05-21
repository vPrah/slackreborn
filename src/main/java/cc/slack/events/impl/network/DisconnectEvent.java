package cc.slack.events.impl.network;

import cc.slack.events.Event;

public class DisconnectEvent extends Event {

    private final Side side;

    public DisconnectEvent(Side side) {
        this.side = side;
    }

    public boolean isClient() {
        return side == Side.Client;
    }

    public boolean isServer() {
        return side == Side.Server;
    }

    public enum Side {
        Client, Server;
    }
}