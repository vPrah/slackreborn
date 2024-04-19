package cc.slack.events.impl.player;

import cc.slack.events.Event;
import cc.slack.utils.client.mc;
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
        mc.getPlayer().motionX = x;
        this.x = x;
    }

    public void setY(double y) {
        mc.getPlayer().motionY = y;
        this.y = y;
    }

    public void setZ(double z) {
        mc.getPlayer().motionZ = z;
        this.z = z;
    }

}
