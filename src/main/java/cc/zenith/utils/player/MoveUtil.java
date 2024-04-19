package cc.zenith.utils.player;

import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.utils.client.MC;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class MoveUtil extends MC {

    public static void setSpeed(MoveEvent event, double speed) {
        setBaseSpeed(event, speed, getPlayer().rotationYaw, getPlayer().moveForward, getPlayer().moveStrafing);
    }

    public static void setSpeed(MoveEvent event, double speed, float yawDegrees) {
        setBaseSpeed(event, speed, yawDegrees, getPlayer().moveForward, getPlayer().moveStrafing);
    }

    private static void setBaseSpeed(MoveEvent event, double speed, float yaw, double forward, double strafing) {
        if (getPlayer() != null && getWorld() != null) {
            final boolean reversed = forward < 0.0f;
            final float strafingYaw = 90.0f * (forward > 0.0f ? 0.5f : reversed ? -0.5f : 1.0f);

            if (reversed)
                yaw += 180.0f;
            if (strafing > 0.0f)
                yaw -= strafingYaw;
            else if (strafing < 0.0f)
                yaw += strafingYaw;

            final double finalX = Math.cos(Math.toRadians(yaw + 90.0f)) * speed;
            final double finalZ = Math.cos(Math.toRadians(yaw)) * speed;

            if (event != null) {
                if (isMoving()) {
                    event.setX(finalX);
                    event.setZ(finalZ);
                } else
                    event.setZeroXZ();
            } else {
                if (isMoving()) {
                    getPlayer().motionX = finalX;
                    getPlayer().motionZ = finalZ;
                } else
                    MoveUtil.resetMotion(false);
            }
        }
    }

    public static boolean isMoving() {
        return getPlayer() != null && (getPlayer().moveForward != 0 || getPlayer().moveStrafing != 0);
    }

    public static float getDirection() {
        return getDirection(getPlayer().rotationYaw, getPlayer().moveForward, getPlayer().moveStrafing);
    }

    public static float getDirection(float rotationYaw, float moveForward, float moveStrafing) {
        if (moveForward == 0 && moveStrafing == 0) return rotationYaw;

        boolean reversed = moveForward < 0;
        double strafingYaw = 90 * (moveForward > 0 ? .5 : reversed ? -.5 : 1);

        if (reversed)
            rotationYaw += 180.f;
        if (moveStrafing > 0)
            rotationYaw -= strafingYaw;
        else if (moveStrafing < 0)
            rotationYaw += strafingYaw;

        return rotationYaw;
    }

    public static void resetMotion(boolean stopY) {
        getPlayer().motionX = getPlayer().motionZ = 0;
        if (stopY) getPlayer().motionY = 0;
    }

}
