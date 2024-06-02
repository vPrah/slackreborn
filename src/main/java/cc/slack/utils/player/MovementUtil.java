package cc.slack.utils.player;

import cc.slack.events.impl.player.MoveEvent;
import cc.slack.utils.client.mc;
import cc.slack.utils.rotations.RotationUtil;
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

    public static void setMotionSpeed(double speed) {
        float yaw = mc.getPlayer().rotationYaw;
        double forward = mc.getPlayer().moveForward;
        double strafe = mc.getPlayer().moveStrafing;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.getPlayer().motionX = 0;
            mc.getPlayer().motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }

            mc.getPlayer().motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            mc.getPlayer().motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }

    public static void strafe(){
        strafe(getSpeed());
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

    public static boolean isOnGround(double height) {
        return !mc.getWorld().getCollidingBoundingBoxes(mc.getPlayer(), mc.getPlayer().getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
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

    public static float getSpeed() {
        return (float) Math.hypot(getPlayer().motionX, getPlayer().motionZ);
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

    public static void setSpeedWithCheck(double speed) {
        if (speed == 0.0) {
            getPlayer().motionX = 0.0;
            getPlayer().motionZ = 0.0;
        }

        if (isMoving() && speed != 0.0) {
            double dir = Math.toRadians((double)getDirection());
            getPlayer().motionX = -Math.sin(dir) * speed;
            getPlayer().motionZ = Math.cos(dir) * speed;
        }
    }
    public static float getSpeedPotMultiplier(double multiplicator) {
        float speedPotMultiplier = 1F;
        if (mc.getPlayer().isPotionActive(Potion.moveSpeed)) {
            int amp = mc.getPlayer().getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            speedPotMultiplier = 1.0F + (float)multiplicator * (amp + 1);
        }
        return speedPotMultiplier;
    }

    public static double getJumpMotion(float motionY) {
        final Potion potion = Potion.jump;

        if (mc.getPlayer().isPotionActive(potion)) {
            final int amplifier = mc.getPlayer().getActivePotionEffect(potion).getAmplifier();
            motionY += (amplifier + 1) * 0.1F;
        }

        return motionY;
    }
    

    public static void setVClip(double number) {
        mc.getPlayer().setPosition(mc.getPlayer().posX, mc.getPlayer().posY + number, mc.getPlayer().posZ);
    }

    public static void setHClip(double offset) {
        mc.getPlayer().setPosition(mc.getPlayer().posX + -MathHelper.sin(getBindsDirection(mc.getPlayer().rotationYaw)) * offset, mc.getPlayer().posY, mc.getPlayer().posZ + MathHelper.cos(getBindsDirection(mc.getPlayer().rotationYaw)) * offset);
    }

    public static void updateBinds() {
        updateBinds(true);
    }

    public static void updateBinds(boolean checkGui) {
        if (checkGui && mc.getCurrentScreen() != null) return;
        mc.getGameSettings().keyBindJump.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindJump);
        mc.getGameSettings().keyBindSprint.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindSprint);
        mc.getGameSettings().keyBindForward.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindForward);
        mc.getGameSettings().keyBindRight.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindRight);
        mc.getGameSettings().keyBindBack.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindBack);
        mc.getGameSettings().keyBindLeft.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindLeft);
    }

}
