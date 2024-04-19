package cc.zenith.events.impl.player;

import cc.zenith.events.Event;
import cc.zenith.utils.client.MC;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoveEvent extends Event {

    private double x, y, z;

    public void setZero() {
        setX(0);
        setZ(0);
        setY(0);
    }

    public void setZeroXZ() {
        setX(0);
        setZ(0);
    }

    public void setX(double x) {
        MC.getPlayer().motionX = x;
        this.x = x;
    }

    public void setY(double y) {
        MC.getPlayer().motionY = y;
        this.y = y;
    }

    public void setZ(double z) {
        MC.getPlayer().motionZ = z;
        this.z = z;
    }

}
