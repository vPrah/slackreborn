package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.utils.client.MC;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.potion.Potion;


@ModuleInfo(
        name = "Sprint",
        category = Category.MOVEMENT
)
public class Sprint extends Module {

    //   Split Like This Cuz I Don't Want A Long Af Boolean

    private boolean hasPotion() {
        return !(MC.getPlayer().isPotionActive(Potion.confusion) ||
                MC.getPlayer().isPotionActive(Potion.blindness));
    }

    private boolean isMoving() {
        return !MC.getPlayer().isCollidedHorizontally &&
                (MC.getPlayer().getFoodStats().getFoodLevel() > 6 || MC.getPlayer().capabilities.allowFlying) &&
                !MC.getPlayer().isSneaking() && (!MC.getPlayer().isUsingItem());
    }

    @Listen
    public void onUpdate(UpdateEvent e) {
        MC.getPlayer().setSprinting(MC.getPlayer().moveForward > 0 && isMoving() && hasPotion());
    }

}
