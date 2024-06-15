package cc.slack.events.impl.player;

import cc.slack.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoveEvent extends Event {

    private double x, y, z;
    public boolean safewalk = false;

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
        mc.thePlayer.motionX = x;
        this.x = x;
    }

    public void setY(double y) {
        mc.thePlayer.motionY = y;
        this.y = y;
    }

    public void setZ(double z) {
        mc.thePlayer.motionZ = z;
        this.z = z;
    }

}
