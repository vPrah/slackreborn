package cc.slack.utils.player;

import cc.slack.utils.client.mc;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtil extends mc {

    public static float[] getNeededRotations(final Vec3 vec) {
        final Vec3 playerVector = new Vec3(getPlayer().posX, getPlayer().posY + getPlayer().getEyeHeight(), getPlayer().posZ);
        final double y = vec.yCoord - playerVector.yCoord;
        final double x = vec.xCoord - playerVector.xCoord;
        final double z = vec.zCoord - playerVector.zCoord;

        final double dff = Math.sqrt(x * x + z * z);
        final float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90;
        final float pitch = (float) -Math.toDegrees(Math.atan2(y, dff));

        return new float[]{
                updateRots(yaw, yaw, 180),
                updateRots(pitch, pitch, 90)
        };
    }

    public static Vec3 getBlockVecCenter(final BlockPos pos) {
        return (new Vec3(pos)).addVector(0.5D, 0.5D, 0.5D);
    }

    public static float[] getRotations(final Vec3 start, final Vec3 dst) {
        final double xDif = dst.xCoord - start.xCoord;
        final double yDif = dst.yCoord - start.yCoord;
        final double zDif = dst.zCoord - start.zCoord;

        final double distXZ = Math.sqrt(xDif * xDif + zDif * zDif);

        return new float[]{
                (float) (Math.atan2(zDif, xDif) * 180 / Math.PI) - 90,
                (float) (-(Math.atan2(yDif, distXZ) * 180 / Math.PI))
        };
    }

    public static float[] getRotations(double x, double y, double z) {
        final Vec3 lookVec = getPlayer().getPositionEyes(1.0f);
        final double dx = lookVec.xCoord - x;
        final double dy = lookVec.yCoord - y;
        final double dz = lookVec.zCoord - z;

        final double dist = Math.hypot(dx, dz);
        final double yaw = Math.toDegrees(Math.atan2(dz, dx));
        final double pitch = Math.toDegrees(Math.atan2(dy, dist));

        return new float[]{
                (float) yaw + 90,
                (float) pitch
        };
    }

    public static float[] getRotations(final float[] lastRotations, final float smoothing, final Vec3 start, final Vec3 dst) {
        final float[] rotations = getRotations(start, dst);
        applySmoothing(lastRotations, smoothing, rotations);

        return rotations;
    }

    public static void applySmoothing(final float[] lastRotations, final float smoothing, final float[] dstRotation) {
        if (smoothing > 0) {
            final float yawChange = MathHelper.wrapAngleTo180_float(dstRotation[0] - lastRotations[0]);
            final float pitchChange = MathHelper.wrapAngleTo180_float(dstRotation[1] - lastRotations[1]);

            final float smoothingFactor = Math.max(1.0F, smoothing / 10.0F);

            dstRotation[0] = lastRotations[0] + yawChange / smoothingFactor;
            dstRotation[1] = Math.max(Math.min(90.0F, lastRotations[1] + pitchChange / smoothingFactor), -90.0F);
        }
    }

    public static float[] applyGCD(final float[] rotations, final float[] prevRots) {
        final float mouseSensitivity = (float) (mc.getGameSettings().mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F + 0.2F);
        final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
        final float yaw = prevRots[0] + (float) (Math.round((rotations[0] - prevRots[0]) / multiplier) * multiplier);
        final float pitch = prevRots[1] + (float) (Math.round((rotations[1] - prevRots[1]) / multiplier) * multiplier);

        return new float[]{yaw, MathHelper.clamp_float(pitch, -90, 90)};
    }

    public static float updateRots(float from, float to, float speed) {
        float f = MathHelper.wrapAngleTo180_float(to - from);
        if (f > speed) f = speed;
        if (f < -speed) f = -speed;
        return from + f;
    }

    public static EnumFacing getEnumDirection(float yaw) {
        return EnumFacing.getHorizontal(MathHelper.floor_double((double)(yaw * 4.0F / 360.0F) + 0.5D) & 3);
    }
}
