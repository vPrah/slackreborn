package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.player.MovementUtil;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "Strafe",
        category = Category.MOVEMENT
)
public class Strafe extends Module {

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
    }

    @Listen
    public void onMotion (MotionEvent event) {
        MovementUtil.strafe();
    }

}
