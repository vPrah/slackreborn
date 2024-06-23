package cc.slack.features.modules.impl.utilties;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(
        name = "AntiAFK",
        category = Category.UTILITIES
)
public class AntiAfk extends Module {

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {
        switch (mc.thePlayer.ticksExisted % 60) {
            case 0:
                mc.gameSettings.keyBindForward.pressed = true;
                break;
            case 1:
                mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
                mc.gameSettings.keyBindBack.pressed = true;
                break;
            case 2:
                mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
                break;
        }
    }

}
