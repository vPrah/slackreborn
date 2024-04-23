package cc.slack.utils.other;

import cc.slack.utils.client.mc;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;


public class BlockUtils extends mc {
    public Block getBlock(Vec3 vec3) {
        return getBlock(new BlockPos(vec3));
    }
    public static Block getBlock(BlockPos blockPos) {
        if (mc.getWorld() != null && blockPos != null) {
            return mc.getWorld().getBlockState(blockPos).getBlock();
        }
        return null;
    }

    public static Material getMaterial(BlockPos blockPos) {
        Block block = getBlock(blockPos);
        if (block != null) {
            return block.getMaterial();
        }
        return null;
    }

    public static boolean isReplaceable(BlockPos blockPos) {
        Material material = getMaterial(blockPos);
        return material != null && material.isReplaceable();
    }

    public IBlockState getState(BlockPos blockPos) {
        World mc = Minecraft.getMinecraft().theWorld;
        return mc.getBlockState(blockPos);
    }

    public boolean canBeClicked(BlockPos blockPos) {
        return (getBlock(blockPos) != null && Objects.requireNonNull(getBlock(blockPos)).canCollideCheck(getState(blockPos), false)) &&
                mc.getWorld().getWorldBorder().contains(blockPos);
    }

    public static String getBlockName(int id) {
        return Block.getBlockById(id).getLocalizedName();
    }

    public boolean isFullBlock(BlockPos blockPos) {
        AxisAlignedBB axisAlignedBB = getBlock(blockPos) != null ? getBlock(blockPos).getCollisionBoundingBox(mc.getWorld(), blockPos, getState(blockPos)) : null;
        if (axisAlignedBB == null) return false;
        return axisAlignedBB.maxX - axisAlignedBB.minX == 1.0 && axisAlignedBB.maxY - axisAlignedBB.minY == 1.0 && axisAlignedBB.maxZ - axisAlignedBB.minZ == 1.0;
    }

    public double getCenterDistance(BlockPos blockPos) {
        return mc.getPlayer().getDistance(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }

    public static Map<BlockPos, Block> searchBlocks(int radius) {
        Map<BlockPos, Block> blocks = new HashMap<>();

        for (int x = radius; x >= -radius + 1; x--) {
            for (int y = radius; y >= -radius + 1; y--) {
                for (int z = radius; z >= -radius + 1; z--) {
                    BlockPos blockPos = new BlockPos(mc.getPlayer().posX + x, mc.getPlayer().posY + y, mc.getPlayer().posZ + z);
                    Block block = getBlock(blockPos);
                    if (block != null) {
                        blocks.put(blockPos, block);
                    }
                }
            }
        }

        return blocks;
    }

    public boolean collideBlock(AxisAlignedBB axisAlignedBB, Function<Block, Boolean> collide) {
        for (int x = MathHelper.floor_double(mc.getPlayer().getEntityBoundingBox().minX); x <
                MathHelper.floor_double(mc.getPlayer().getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(mc.getPlayer().getEntityBoundingBox().minZ); z <
                    MathHelper.floor_double(mc.getPlayer().getEntityBoundingBox().maxZ) + 1; z++) {
                Block block = getBlock(new BlockPos(x, axisAlignedBB.minY, z));

                if (!collide.apply(block)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean collideBlockIntersects(AxisAlignedBB axisAlignedBB, Predicate<Block> collide) {
        for (int x = MathHelper.floor_double(mc.getPlayer().getEntityBoundingBox().minX); x <
                MathHelper.floor_double(mc.getPlayer().getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(mc.getPlayer().getEntityBoundingBox().minZ); z <
                    MathHelper.floor_double(mc.getPlayer().getEntityBoundingBox().maxZ) + 1; z++) {
                BlockPos blockPos = new BlockPos(x, axisAlignedBB.minY, z);
                Block block = getBlock(blockPos);

                if (collide.test(block)) {
                    AxisAlignedBB boundingBox = block != null ? block.getCollisionBoundingBox(mc.getWorld(), blockPos, getState(blockPos)) : null;
                    if (boundingBox == null) continue;

                    if (mc.getPlayer().getEntityBoundingBox().intersectsWith(boundingBox)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static Vec3 floorVec3(Vec3 vec3) {
        return new Vec3(Math.floor(vec3.xCoord), Math.floor(vec3.yCoord), Math.floor(vec3.zCoord));
    }
}
