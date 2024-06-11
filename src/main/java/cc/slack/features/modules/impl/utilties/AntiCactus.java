package cc.slack.features.modules.impl.utilties;

import cc.slack.events.impl.player.CollideEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCactus;
import net.minecraft.util.AxisAlignedBB;

@ModuleInfo(
        name = "AntiCactus",
        category = Category.UTILITIES
)
public class AntiCactus extends Module {

    @Listen
    public void onCollide (CollideEvent event) {
        if (event.getBlock() instanceof BlockCactus) {
            event.setBoundingBox(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1, event.getY() + 1, event.getZ() + 1));
        }
    }

}
