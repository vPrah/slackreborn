package cc.zenith.events.impl.game;

import cc.zenith.events.Event;
import cc.zenith.events.State;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TickEvent extends Event {

    private State state;

    public TickEvent() {
        this.state = State.PRE;
    }
}