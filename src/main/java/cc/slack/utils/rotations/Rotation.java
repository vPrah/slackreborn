package cc.slack.utils.rotations;

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
        mc.thePlayer.rotationYaw = yaw;
        mc.thePlayer.rotationPitch = pitch;
    }
}
