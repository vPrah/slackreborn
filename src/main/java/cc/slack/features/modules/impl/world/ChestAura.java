// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.world;

import cc.slack.Slack;
import cc.slack.events.State;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.WorldEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.features.modules.impl.ghost.AutoTool;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.other.BlockUtils;
import cc.slack.utils.other.PrintUtil;
import cc.slack.utils.other.TimeUtil;
import cc.slack.utils.player.AttackUtil;
import cc.slack.utils.render.RenderUtil;
import cc.slack.utils.rotations.RotationUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockChest;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.ILockableContainer;

import java.awt.*;
import java.util.ArrayList;

@ModuleInfo(
        name = "Chest Aura",
        category = Category.WORLD
)
public class ChestAura extends Module {
    public final NumberValue<Double> radiusDist = new NumberValue<>("Radius", 4.5, 1.0, 7.0, 0.5);
    public final NumberValue<Integer> switchDelay = new NumberValue<>("Switch Delay", 50, 0, 500, 10);

    public final BooleanValue onlyGround = new BooleanValue("Only Ground", false);

    public ChestAura() {
        addSettings(radiusDist, switchDelay);
    }

    ArrayList<BlockPos> openedChests = new ArrayList<>();
    TimeUtil switchTimer = new TimeUtil();
    BlockPos currentBlock;

    @Override
    public void onEnable() {
        openedChests.clear();
        switchTimer.reset();
    }

    @Listen
    public void onWorld(WorldEvent event) {
        openedChests.clear(); // its fucing 2 am bro
        switchTimer.reset();
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (Slack.getInstance().getModuleManager().getInstance(Scaffold.class).isToggle()) return;
        if (AttackUtil.inCombat) return;

        if (currentBlock == null && switchTimer.hasReached(switchDelay.getValue())) {
            currentBlock = findNextBlock();
            if (currentBlock != null) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), currentBlock, EnumFacing.UP, new Vec3(0.5, 1, 0.5))) {
                    mc.thePlayer.swingItem();
                    RotationUtil.setClientRotation(BlockUtils.getFaceRotation(EnumFacing.UP, currentBlock));
                    openedChests.add(currentBlock);
                }
            }
        } else if (!(mc.currentScreen instanceof GuiChest)) {
            currentBlock = null;
            switchTimer.reset();
        }
    }

    private BlockPos findNextBlock(){
        int radius = (int) Math.ceil(radiusDist.getValue());

        for(int x = radius; x >=-radius +1; x--) {
            for (int y = radius; y >= -radius + 1; y--) {
                for (int z = radius; z >= -radius + 1; z--) {
                    BlockPos blockPos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                    Block block = BlockUtils.getBlock(blockPos);
                    if (block != null) {
                        if (block instanceof BlockChest) {
                            if (!openedChests.contains(blockPos)) {
                                return blockPos;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

}
