package cc.zenith.utils.player;

import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.utils.client.MC;
import cc.zenith.utils.network.PacketUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class PlayerUtil extends MC {

    public static final double BASE_MOTION = 0.21585904623731839;
    public static final double BASE_MOTION_SPRINT = 0.28060730580133125;
    public static final double BASE_MOTION_WATER = 0.09989148404308008;
    public static final double MAX_MOTION_SPRINT = 0.28623662093593094;
    public static final double BASE_GROUND_BOOST = 1.9561839658913562;
    public static final double BASE_GROUND_FRICTION = 0.587619839258055;
    public static final double SPEED_GROUND_BOOST = 2.016843005849186;
    public static final double MOVE_FRICTION = 0.9800000190734863;

    public static double getJumpHeight(double height) {
        if (getPlayer().isPotionActive(Potion.jump))
            return height + (getPlayer().getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;

        return height;
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

    public static boolean isOnSameTeam(EntityPlayer entity) {
        if (entity.getTeam() != null && getPlayer().getTeam() != null)
            return entity.getDisplayName().getFormattedText().charAt(1) == getPlayer().getDisplayName().getFormattedText().charAt(1);
        else
            return false;
    }

    public static boolean isBlockUnder() {
        for (int y = (int) getPlayer().posY; y >= 0; y--) {
            if (!(getWorld().getBlockState
                    (new BlockPos(getPlayer().posX, y, getPlayer().posZ)).getBlock() instanceof BlockAir))
                return true;
        }

        return false;
    }

    public static boolean isOnLiquid() {
        boolean onLiquid = false;
        AxisAlignedBB playerBB = getPlayer().getEntityBoundingBox();
        double y = (int) playerBB.offset(0.0, -0.01, 0.0).minY;

        for (double x = MathHelper.floor_double(playerBB.minX); x < MathHelper.floor_double(playerBB.maxX) + 1; ++x) {
            for (double z = MathHelper.floor_double(playerBB.minZ); z < MathHelper.floor_double(playerBB.maxZ) + 1; ++z) {
                final Block block = getWorld().getBlockState(new BlockPos(x, y, z)).getBlock();

                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) return false;

                    onLiquid = true;
                }
            }
        }

        return onLiquid;
    }

    public static boolean isOverVoid() {
        for (double posY = getPlayer().posY; posY > 0.0; posY--) {
            if (!(getWorld().getBlockState(
                    new BlockPos(getPlayer().posX, posY, getPlayer().posZ)).getBlock() instanceof BlockAir))
                return false;
        }

        return true;
    }

    public static double getMaxFallDist() {
        double fallDistanceReq = 3.1;

        if (getPlayer().isPotionActive(Potion.jump)) {
            int amplifier = getPlayer().getActivePotionEffect(Potion.jump).getAmplifier();
            fallDistanceReq += (float) (amplifier + 1);
        }

        return fallDistanceReq;
    }

    public static void damagePlayer(double value, boolean groundCheck) {
        if (!groundCheck || getPlayer().onGround) {
            for (int i = 0; i < Math.ceil(PlayerUtil.getMaxFallDist() / value); i++) {
                PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY + value, getPlayer().posZ, false));
                PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY, getPlayer().posZ, false));
            }

            PacketUtil.sendNoEvent(new C03PacketPlayer(true));
        }
    }

}
