package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.potion.Potion;


@ModuleInfo(
        name = "Sprint",
        category = Category.MOVEMENT
)
public class Sprint extends Module {

    //   Split Like This Cuz I Don't Want A Long Af Boolean

    private boolean hasPotion() {
        return !(mc.getPlayer().isPotionActive(Potion.confusion) ||
                mc.getPlayer().isPotionActive(Potion.blindness));
    }

    private boolean isMoving() {
        return !mc.getPlayer().isCollidedHorizontally &&
                (mc.getPlayer().getFoodStats().getFoodLevel() > 6 || mc.getPlayer().capabilities.allowFlying) &&
                !mc.getPlayer().isSneaking() && (!mc.getPlayer().isUsingItem());
    }

    @Listen
    public void onUpdate(UpdateEvent e) {
        mc.getPlayer().setSprinting(mc.getPlayer().moveForward > 0 && isMoving() && hasPotion());
    }

}
