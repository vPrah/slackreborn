package cc.slack.utils.player;

import cc.slack.events.impl.player.MoveEvent;
import cc.slack.utils.client.mc;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class MovementUtil extends mc {

    public static void setSpeed(MoveEvent event, double speed) {
        setBaseSpeed(event, speed, getPlayer().rotationYaw, getPlayer().moveForward, getPlayer().moveStrafing);
    }

    public static void setSpeed(MoveEvent event, double speed, float yawDegrees) {
        setBaseSpeed(event, speed, yawDegrees, getPlayer().moveForward, getPlayer().moveStrafing);
    }

    public static void minLimitStrafe(float speed) {
        strafe();
        if (getSpeed() < speed) {
            strafe(speed);
        }
    }

    public static void strafe(){
        strafe((float) getSpeed());
    }

    public static void strafe(float speed) {
        final float yaw = getDirection();

        getPlayer().motionX = Math.cos(Math.toRadians(yaw + 90.0f)) * speed;
        getPlayer().motionZ = Math.cos(Math.toRadians(yaw)) * speed;
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
                    MovementUtil.resetMotion(false);
            }
        }
    }

    public static boolean isMoving() {
        return getPlayer() != null && (getPlayer().moveForward != 0 || getPlayer().moveStrafing != 0);
    }

    public static boolean isBindsMoving() {
        return getPlayer() != null && (
                GameSettings.isKeyDown(mc.getGameSettings().keyBindForward) ||
                GameSettings.isKeyDown(mc.getGameSettings().keyBindRight) ||
                GameSettings.isKeyDown(mc.getGameSettings().keyBindBack) ||
                GameSettings.isKeyDown(mc.getGameSettings().keyBindLeft)
        );
    }

    public static float getDirection() {
        if (RotationUtil.isEnabled && RotationUtil.strafeFix) return getBindsDirection(getPlayer().rotationYaw);
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

    public static float getBindsDirection(float rotationYaw) {
        int moveForward = 0;
        if (GameSettings.isKeyDown(mc.getGameSettings().keyBindForward)) moveForward++;
        if (GameSettings.isKeyDown(mc.getGameSettings().keyBindBack)) moveForward--;

        int moveStrafing = 0;
        if (GameSettings.isKeyDown(mc.getGameSettings().keyBindRight)) moveStrafing++;
        if (GameSettings.isKeyDown(mc.getGameSettings().keyBindLeft)) moveStrafing--;

        boolean reversed = moveForward < 0;
        double strafingYaw = 90 * (moveForward > 0 ? .5 : reversed ? -.5 : 1);

        if (reversed)
            rotationYaw += 180.f;
        if (moveStrafing > 0)
            rotationYaw += strafingYaw;
        else if (moveStrafing < 0)
            rotationYaw -= strafingYaw;

        return rotationYaw;
    }

    public static void resetMotion() {
        getPlayer().motionX = getPlayer().motionZ = 0;
    }

    public static void resetMotion(boolean stopY) {
        getPlayer().motionX = getPlayer().motionZ = 0;
        if (stopY) getPlayer().motionY = 0;
    }

    public static double getSpeed() {
        return Math.hypot(getPlayer().motionX, getPlayer().motionZ);
    }

    public static double getSpeed(MoveEvent event) {
        return Math.hypot(event.getX(), event.getZ());
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;

        if (getPlayer().isPotionActive(Potion.moveSpeed)) {
            double amplifier = getPlayer().getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }

        return baseSpeed;
    }

    public static void setVClip(double number) {
        mc.getPlayer().setPosition(mc.getPlayer().posX, mc.getPlayer().posY + number, mc.getPlayer().posZ);
    }

    public static void setHClip(double offset) {
        mc.getPlayer().setPosition(mc.getPlayer().posX + -MathHelper.sin(getDirection()) * offset, mc.getPlayer().posY, mc.getPlayer().posZ + MathHelper.cos(getDirection()) * offset);
    }

    public static void updateBinds() {
        mc.getGameSettings().keyBindJump.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindJump);
        mc.getGameSettings().keyBindSprint.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindSprint);
        mc.getGameSettings().keyBindForward.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindForward);
        mc.getGameSettings().keyBindRight.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindRight);
        mc.getGameSettings().keyBindBack.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindBack);
        mc.getGameSettings().keyBindLeft.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindLeft);
    }

}
