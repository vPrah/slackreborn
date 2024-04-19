package cc.zenith.events.impl.player;

import cc.zenith.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StrafeEvent extends Event {
    private float strafe;
    private float forward;
    private float friction;
    private float yaw;
}