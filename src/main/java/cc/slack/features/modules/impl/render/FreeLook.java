package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.game.TickEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import cc.slack.utils.render.FreeLookUtil;
import io.github.nevalackin.radbus.Listen;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "FreeLook",
        category = Category.RENDER,
        key = Keyboard.KEY_X
)
public class FreeLook extends Module {

    private boolean freeLookingactivated;

    @SuppressWarnings("unused")
    @Listen
    public void onTick (TickEvent event) {
        if (mc.getPlayer().ticksExisted < 10) {
            stop();
        }
        if (Keyboard.isKeyDown(getKey())) {
            freeLookingactivated = true;
            FreeLookUtil.setFreelooking(true);
            mc.getGameSettings().thirdPersonView = 1;
        } else if (freeLookingactivated) {
            stop();
        }
    }

    private void stop() {
        toggle();
        FreeLookUtil.setFreelooking(false);
        freeLookingactivated = false;
        mc.getGameSettings().thirdPersonView = 0;
    }
}
