package cc.slack.utils.player;

import cc.slack.utils.client.mc;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import java.util.Arrays;
import java.util.List;


public final class InventoryUtils extends mc {

    public static final List<Block> BLOCK_BLACKLIST = Arrays.asList(
            Blocks.enchanting_table,
            Blocks.chest,
            Blocks.ender_chest,
            Blocks.trapped_chest,
            Blocks.anvil,
            Blocks.sand,
            Blocks.web,
            Blocks.torch,
            Blocks.crafting_table,
            Blocks.furnace,
            Blocks.waterlily,
            Blocks.dispenser,
            Blocks.stone_pressure_plate,
            Blocks.wooden_pressure_plate,
            Blocks.noteblock,
            Blocks.dropper,
            Blocks.tnt,
            Blocks.standing_banner,
            Blocks.wall_banner,
            Blocks.redstone_torch,
            Blocks.gravel,
            Blocks.cactus,
            Blocks.bed,
            Blocks.lever,
            Blocks.standing_sign,
            Blocks.wall_sign,
            Blocks.jukebox,
            Blocks.oak_fence,
            Blocks.spruce_fence,
            Blocks.birch_fence,
            Blocks.jungle_fence,
            Blocks.dark_oak_fence,
            Blocks.oak_fence_gate,
            Blocks.spruce_fence_gate,
            Blocks.birch_fence_gate,
            Blocks.jungle_fence_gate,
            Blocks.dark_oak_fence_gate,
            Blocks.nether_brick_fence,
            //Blocks.cake,
            Blocks.trapdoor,
            Blocks.melon_block,
            Blocks.brewing_stand,
            Blocks.cauldron,
            Blocks.skull,
            Blocks.hopper,
            Blocks.carpet,
            Blocks.redstone_wire,
            Blocks.light_weighted_pressure_plate,
            Blocks.heavy_weighted_pressure_plate,
            Blocks.daylight_detector
    );

    public static int findItem(final int startSlot, final int endSlot, final Item item) {
        for (int i = startSlot; i < endSlot; i++) {
            final ItemStack stack = mc.getPlayer().inventoryContainer.getSlot(i).getStack();

            if (stack != null && stack.getItem() == item)
                return i;
        }
        return -1;
    }

    public static boolean isHotbarFull() {
        for (int i = 36; i < 45; i++) {
            if (mc.getPlayer().inventoryContainer.getSlot(i).getStack() == null)
                return false;
        }
        return true;
    }

    public static int pickHotarBlock(boolean biggestStack) {
        if (biggestStack) {
            int currentStackSize = 0;
            int currentSlot = 36;
            for (int i = 36; i < 45; i++) {
                final ItemStack itemStack = mc.getPlayer().inventoryContainer.getSlot(i).getStack();

                if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > currentStackSize) {
                    final Block block = ((ItemBlock) itemStack.getItem()).getBlock();

                    if (block.isFullCube() && !BLOCK_BLACKLIST.contains(block)) {
                        currentStackSize = itemStack.stackSize;
                        currentSlot = i;
                    }
                }
            }

            if (currentStackSize > 0) {
                return currentSlot;
            }
        } else {
            for (int i = 36; i < 45; i++) {
                final ItemStack itemStack = mc.getPlayer().inventoryContainer.getSlot(i).getStack();

                if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > 0) {
                    final Block block = ((ItemBlock) itemStack.getItem()).getBlock();

                    if (block.isFullCube() && !BLOCK_BLACKLIST.contains(block))
                        return i;
                }
            }
        }
        return -1;
    }
}