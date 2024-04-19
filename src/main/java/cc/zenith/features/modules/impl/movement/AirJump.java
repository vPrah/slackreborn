package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.CollideEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.utils.client.MC;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.AxisAlignedBB;


@ModuleInfo(
        name = "AirJump",
        category = Category.MOVEMENT
)
public class AirJump extends Module {

    double startY;

    @Override
    public void onEnable() {
        startY = MC.getPlayer().posY;
    }

    @Listen
    public void onMove(MoveEvent event) {
        if (MC.getGameSettings().keyBindJump.isPressed() && MC.getPlayer().onGround) {
            event.setY(0.42F);
        }
    }

    @Listen
    public void onCollide(CollideEvent event) {
        if (event.getBlock() instanceof BlockAir && event.getY() <= startY)
            event.setBoundingBox(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1, startY, event.getZ() + 1));
    }
}
