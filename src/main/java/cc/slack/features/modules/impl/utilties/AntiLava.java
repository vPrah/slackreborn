package cc.slack.features.modules.impl.utilties;

import cc.slack.events.State;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.other.MathUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

@ModuleInfo(
        name = "AntiLava",
        category = Category.UTILITIES
)
public class AntiLava extends Module {

    boolean cancelNext = false;

    @Override
    public void onEnable() {
        cancelNext = false;
    }

    @Listen
    public void onMotion (MotionEvent event) {
        if (event.getState() != State.POST) {
            Block block = mc.getWorld().getBlockState(new BlockPos(mc.thePlayer.posX + mc.thePlayer.motionX, mc.thePlayer.posY + (mc.thePlayer.onGround ? Math.max(0.0, mc.thePlayer.motionY) : mc.thePlayer.motionY), mc.thePlayer.posZ + mc.thePlayer.motionZ)).getBlock();
            if (block == Blocks.lava || block == Blocks.flowing_lava) {
                event.setY(event.getY() + MathUtil.randomDouble(0.5, 0.85));
            }
        }
    }


}
