package cc.slack.features.modules.impl.utilties;

import cc.slack.events.State;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
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
            Block block = mc.getWorld().getBlockState(new BlockPos(mc.getPlayer().posX + mc.getPlayer().motionX, mc.getPlayer().posY + (mc.getPlayer().onGround ? Math.max(0.0, mc.getPlayer().motionY) : mc.getPlayer().motionY), mc.getPlayer().posZ + mc.getPlayer().motionZ)).getBlock();
            if (block == Blocks.lava || block == Blocks.flowing_lava) {
                event.setY(event.getY() + MathUtil.randomDouble(0.5, 0.85));
            }
        }
    }


}
