package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
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
