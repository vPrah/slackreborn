// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.world;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.features.modules.impl.ghost.AutoTool;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.BlockUtils;
import cc.slack.utils.other.TimeUtil;
import cc.slack.utils.player.RotationUtil;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;

@ModuleInfo(
        name = "Breaker",
        category = Category.WORLD
)
public class Breaker extends Module {
    public final ModeValue<String> mode = new ModeValue<>("Bypass", new String[]{"Hypixel", "None"});
    public final NumberValue<Double> radiusDist = new NumberValue<>("Radius", 4.5, 1.0, 7.0, 0.5);
    public final ModeValue<String> sortMode = new ModeValue<>("Sort", new String[]{"Distance", "Absolute"});
    public final NumberValue<Integer> switchDelay = new NumberValue<>("Switch Delay", 50, 0, 500, 10);
    public final NumberValue<Integer> targetSwitchDelay = new NumberValue<>("Target Switch Delay", 50, 0, 500, 10);

    public Breaker() {
        addSettings(mode, radiusDist, sortMode, switchDelay, targetSwitchDelay);
    }

    private BlockPos targetBlock;
    private BlockPos currentBlock;

    private float breakingProgress;

    private TimeUtil switchTimer = new TimeUtil();

    @Override
    public void onEnable() {
        targetBlock = null;
        currentBlock = null;
        breakingProgress = 0;
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (targetBlock == null) {
            if (switchTimer.hasReached(targetSwitchDelay.getValue())) {
                findTargetBlock();
            }
        } else {
            if (currentBlock == null) {
                if (switchTimer.hasReached(switchDelay.getValue())) {
                    findBreakBlock();
                    breakingProgress = 0f;
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, currentBlock, EnumFacing.DOWN));
                    RotationUtil.overrideRotation(BlockUtils.getCenterRotation(currentBlock));
                }
            }

            if (currentBlock != null) {
                Slack.getInstance().getModuleManager().getInstance(AutoTool.class).getTool(true, BlockUtils.getBlock(currentBlock), 0, false);

                breakingProgress += BlockUtils.getHardness(currentBlock);
                mc.getWorld().sendBlockBreakProgress(mc.getPlayer().getEntityId(), currentBlock, (int) (breakingProgress * 10) - 1);
                mc.getPlayer().swingItem();

                RotationUtil.setClientRotation(BlockUtils.getCenterRotation(currentBlock));
                RotationUtil.setStrafeFix(true, false);

                if (breakingProgress > 1) {
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentBlock, EnumFacing.DOWN));
                    Slack.getInstance().getModuleManager().getInstance(AutoTool.class).getTool(false, BlockUtils.getBlock(currentBlock), 0, false);

                    mc.getPlayerController().onPlayerDestroyBlock(currentBlock, EnumFacing.DOWN );
                    mc.getWorld().setBlockState(currentBlock, Blocks.air.getDefaultState(), 11);
                    if (currentBlock == targetBlock) {
                        targetBlock = null;
                    }
                    currentBlock = null;
                    switchTimer.reset();
                } else {
                    if (BlockUtils.getCenterDistance(currentBlock) > radiusDist.getValue()) {
                        currentBlock = null;
                    }
                    if (BlockUtils.getCenterDistance(targetBlock) > radiusDist.getValue()) {
                        currentBlock = null;
                        targetBlock = null;
                    }
                }
            }
        }
    }

    @Listen
    public void onRender(RenderEvent event) {
        if (event.getState() == RenderEvent.State.RENDER_2D && currentBlock != null) {
            ScaledResolution sr = mc.getScaledResolution();
            String displayString = (int) (breakingProgress * 100) + "%";
            mc.getFontRenderer().drawString(displayString, (sr.getScaledWidth() - mc.getFontRenderer().getStringWidth(displayString)) / 2f, sr.getScaledHeight() / 2f + 20, new Color(255, 255, 255).getRGB(), true);

        }


        if (event.getState() != RenderEvent.State.RENDER_3D) return;
        if (currentBlock == null) return;
        RenderUtil.drawBlock(currentBlock, new Color(255,255,255,140));
        RenderUtil.drawBlock(targetBlock, new Color(255,25,25,180));

    }

    private void findTargetBlock() {
        int radius = (int) Math.ceil(radiusDist.getValue());
        BlockPos bestBlock = null;
        double bestDist = -1.0;
        int bestAbs = -1;

        for (int x = radius; x >= -radius + 1; x--) {
            for (int y = radius; y >= -radius + 1; y--) {
                for (int z = radius; z >= -radius + 1; z--) {
                    BlockPos blockPos = new BlockPos(mc.getPlayer().posX + x, mc.getPlayer().posY + y, mc.getPlayer().posZ + z);
                    Block block = BlockUtils.getBlock(blockPos);
                    if (block != null) {
                        if (block instanceof BlockBed) {
                            switch (sortMode.getValue().toLowerCase()) {
                                case "distance":
                                    if (bestDist == -1 || BlockUtils.getCenterDistance(blockPos) < bestDist) {
                                        bestBlock = blockPos;
                                        bestDist = BlockUtils.getCenterDistance(blockPos);
                                    }
                                    break;
                                case "absolute":
                                    if (bestAbs == -1 || BlockUtils.getAbsoluteValue(blockPos) < bestDist) {
                                        bestBlock = blockPos;
                                        bestAbs = BlockUtils.getAbsoluteValue(blockPos);
                                    }
                            }
                        }
                    }
                }
            }
        }

        if (bestBlock != null) {
            targetBlock = bestBlock;
            switchTimer.reset();
        }
    }

    private void findBreakBlock() {
        if (targetBlock == null) return;

        switch (mode.getValue().toLowerCase()) {
            case "hypixel":
                if (BlockUtils.isReplaceableNotBed(targetBlock.east()) ||
                    BlockUtils.isReplaceableNotBed(targetBlock.north()) ||
                    BlockUtils.isReplaceableNotBed(targetBlock.west()) ||
                    BlockUtils.isReplaceableNotBed(targetBlock.south()) ||
                    BlockUtils.isReplaceableNotBed(targetBlock.up())) {
                    currentBlock = targetBlock;
                } else {
                    currentBlock = targetBlock.up();
                }
                break;
            case "none":
                currentBlock = targetBlock;
                break;
        }
    }
}
