package cc.slack.utils.rotations;

import cc.slack.utils.client.mc;
import lombok.Getter;
import lombok.Setter;

public class Rotation {

    @Getter
    @Setter
    private float yaw;
    private float pitch;

    public Rotation() {
        yaw = RotationUtil.clientRotation[0];
        pitch = RotationUtil.clientRotation[1];
    }

    public Rotation(float y, float p) {
        yaw = y;
        pitch = p;
    }

    public void setToPlayer() {
        mc.getPlayer().rotationYaw = yaw;
        mc.getPlayer().rotationPitch = pitch;
    }
}
