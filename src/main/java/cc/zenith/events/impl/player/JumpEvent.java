package cc.zenith.events.impl.player;

import cc.zenith.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JumpEvent extends Event {
    private float yaw;
}
