package cc.slack.utils.player;

import cc.slack.events.impl.player.MoveEvent;
import cc.slack.utils.client.IMinecraft;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.other.BlockUtils;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.Iterator;
import java.util.Map;

public class PlayerUtil implements IMinecraft {

    public static final double BASE_MOTION = 0.21585904623731839;
    public static final double BASE_MOTION_SPRINT = 0.28060730580133125;
    public static final double BASE_MOTION_WATER = 0.09989148404308008;
    public static final double MAX_MOTION_SPRINT = 0.28623662093593094;
    public static final double BASE_GROUND_BOOST = 1.9561839658913562;
    public static final double BASE_GROUND_FRICTION = 0.587619839258055;
    public static final double SPEED_GROUND_BOOST = 2.016843005849186;
    public static final double MOVE_FRICTION = 0.9800000190734863;

    public static final double BASE_JUMP_HEIGHT = 0.41999998688698;
    public static final double HEAD_HITTER_MOTIONY = -0.07840000152;

    public static double getJumpHeight() {
        return getJumpHeight(BASE_JUMP_HEIGHT);
    }

    public static double getJumpHeight(double height) {
        if (mc.thePlayer.isPotionActive(Potion.jump))
            return height + (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;

        return height;
    }

    public static double getSpeed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static double getSpeed(MoveEvent event) {
        return Math.hypot(event.getX(), event.getZ());
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;

        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            double amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }

        return baseSpeed;
    }

    public static boolean isOverAir(double x, double y, double z) {
        return BlockUtils.isAir(new BlockPos(x, y - 1, z));
    }

    public static boolean isOverAir() {
         return isOverAir(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    public static boolean isOnSameTeam(EntityPlayer entity) {
        if (entity.getTeam() != null && mc.thePlayer.getTeam() != null)
            return entity.getDisplayName().getFormattedText().charAt(1) == mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
        else
            return false;
    }

    public static boolean isBlockUnder() {
        for (int y = (int) mc.thePlayer.posY; y >= 0; y--) {
            if (!(mc.theWorld.getBlockState
                    (new BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ)).getBlock() instanceof BlockAir))
                return true;
        }

        return false;
    }

    public static boolean isBlockUnderP(int offset) {
        for (int i = (int) (mc.thePlayer.posY - offset); i > 0; i--) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
            if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir))
                return true;
        }
        return false;
    }

    public static boolean isOnLiquid() {
        boolean onLiquid = false;
        AxisAlignedBB playerBB = mc.thePlayer.getEntityBoundingBox();
        double y = (int) playerBB.offset(0.0, -0.01, 0.0).minY;

        for (double x = MathHelper.floor_double(playerBB.minX); x < MathHelper.floor_double(playerBB.maxX) + 1; ++x) {
            for (double z = MathHelper.floor_double(playerBB.minZ); z < MathHelper.floor_double(playerBB.maxZ) + 1; ++z) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();

                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) return false;

                    onLiquid = true;
                }
            }
        }

        return onLiquid;
    }

    public static boolean isOverVoid() {
        for (double posY = mc.thePlayer.posY; posY > 0.0; posY--) {
            if (!(mc.theWorld.getBlockState(
                    new BlockPos(mc.thePlayer.posX, posY, mc.thePlayer.posZ)).getBlock() instanceof BlockAir))
                return false;
        }

        return true;
    }

    public static double getMaxFallDist() {
        double fallDistanceReq = 3.1;

        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            fallDistanceReq += (float) (amplifier + 1);
        }

        return fallDistanceReq;
    }

    public static void damagePlayer(double value, boolean groundCheck) {
        if (!groundCheck || mc.thePlayer.onGround) {
            for (int i = 0; i < Math.ceil(PlayerUtil.getMaxFallDist() / value); i++) {
                PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + value, mc.thePlayer.posZ, false));
                PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            }

            PacketUtil.sendNoEvent(new C03PacketPlayer(true));
        }
    }

    public static String getRemoteIp() {
        String serverIp = "Main Menu";

        if (mc.isIntegratedServerRunning()) {
            serverIp = "SinglePlayer";
        } else if (mc.theWorld != null && mc.theWorld.isRemote) {
            final ServerData serverData = mc.getCurrentServerData();
            if(serverData != null)
                serverIp = serverData.serverIP;
        }

        return serverIp;
    }

    public static ItemStack getBestSword() {
        int size = mc.thePlayer.inventoryContainer.getInventory().size();
        ItemStack lastSword = null;
        for (int i = 0; i < size; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getInventory().get(i);
            if (stack != null && stack.getItem() instanceof ItemSword)
                if (lastSword == null) {
                    lastSword = stack;
                } else if (isBetterSword(stack, lastSword)) {
                    lastSword = stack;
                }
        }
        return lastSword;
    }


    public static ItemStack getBestAxe() {
        int size = mc.thePlayer.inventoryContainer.getInventory().size();
        ItemStack lastAxe = null;
        for (int i = 0; i < size; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getInventory().get(i);
            if (stack != null && stack.getItem() instanceof ItemAxe)
                if (lastAxe == null) {
                    lastAxe = stack;
                } else if (isBetterTool(stack, lastAxe, Blocks.planks)) {
                    lastAxe = stack;
                }
        }
        return lastAxe;
    }

    public static Block getBlockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.getWorld().getBlockState(new BlockPos(mc.thePlayer.posX + offsetX, mc.thePlayer.posY + offsetY, mc.thePlayer.posZ + offsetZ)).getBlock();
    }

    public static int getBestHotbarTool(Block target) {
        int bestTool = mc.thePlayer.inventory.currentItem;
        for (int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null && mc.thePlayer.inventoryContainer.getSlot(bestTool).getStack() != null) {
                if (isBetterTool(itemStack,  mc.thePlayer.inventoryContainer.getSlot(bestTool).getStack(), target)) {
                    bestTool = i;
                }
            }
        }
        return bestTool;
    }


    public static boolean isBetterTool(ItemStack better, ItemStack than, Block versus) {
        return (getToolDigEfficiency(better, versus) > getToolDigEfficiency(than, versus));
    }

    public static boolean isBetterSword(ItemStack better, ItemStack than) {
        return (getSwordDamage((ItemSword) better.getItem(), better) > getSwordDamage((ItemSword) than.getItem(),
                than));
    }

    public static float getSwordDamage(ItemSword sword, ItemStack stack) {
        float base = sword.getMaxDamage();
        return base + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
    }

    public static float getToolDigEfficiency(ItemStack stack, Block block) {
        float f = stack.getStrVsBlock(block);
        if (f > 1.0F) {
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
            if (i > 0)
                f += (i * i + 1);
        }
        return f;
    }

    public static int bestWeapon(Entity target) {
        Minecraft mc = Minecraft.getMinecraft();
        int firstSlot = mc.thePlayer.inventory.currentItem = 0;
        int bestWeapon = -1;
        int j = 1;

        for(byte i = 0; i < 9; ++i) {
            mc.thePlayer.inventory.currentItem = i;
            ItemStack itemStack = mc.thePlayer.getHeldItem();
            if (itemStack != null) {
                int itemAtkDamage = (int)getItemAtkDamage(itemStack);
                itemAtkDamage = (int)((float)itemAtkDamage + EnchantmentHelper.func_152377_a(itemStack, EnumCreatureAttribute.UNDEFINED));
                if (itemAtkDamage > j) {
                    j = itemAtkDamage;
                    bestWeapon = i;
                }
            }
        }

        if (bestWeapon != -1) {
            return bestWeapon;
        } else {
            return firstSlot;
        }
    }

    public static float getItemAtkDamage(ItemStack itemStack) {
        Multimap multimap = itemStack.getAttributeModifiers();
        if (!multimap.isEmpty()) {
            Iterator iterator = multimap.entries().iterator();
            if (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
                double damage = attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2 ? attributeModifier.getAmount() : attributeModifier.getAmount() * 100.0;
                if (attributeModifier.getAmount() > 1.0) {
                    return 1.0F + (float)damage;
                }

                return 1.0F;
            }
        }

        return 1.0F;
    }

    public static int findRod(int startSlot, int endSlot) {
        for(int i = startSlot; i < endSlot; ++i) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() == Items.fishing_rod) {
                return i;
            }
        }

        return -1;
    }

    public static void switchToRod() {
        for(int i = 36; i < 45; ++i) {
            ItemStack itemstack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemstack != null && Item.getIdFromItem(itemstack.getItem()) == 346) {
                mc.thePlayer.inventory.currentItem = i - 36;
                break;
            }
        }

    }

    public static void switchBack() {
        mc.thePlayer.inventory.currentItem = bestWeapon(mc.thePlayer);
    }

}
