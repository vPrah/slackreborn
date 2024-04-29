// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.player.CollideEvent;
import cc.slack.events.impl.player.MoveEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;


@ModuleInfo(
        name = "AirJump",
        category = Category.MOVEMENT
)
public class AirJump extends Module {

    double startY;

    @Override
    public void onEnable() {
        startY = mc.getPlayer().posY;
    }

    @Listen
    public void onMove(MoveEvent event) {
        if (mc.getGameSettings().keyBindJump.isPressed() && mc.getPlayer().onGround) {
            event.setY(0.42F);
        }
    }

    @SuppressWarnings("unused")
    @Listen
    public void onCollide(CollideEvent event) {
        if (event.getBlock() instanceof BlockAir && event.getY() <= startY)
            event.setBoundingBox(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1, startY, event.getZ() + 1));
    }
}
