package cc.slack.features.modules.impl.utilties;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
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
        switch (mc.getPlayer().ticksExisted % 60) {
            case 0:
                mc.getGameSettings().keyBindForward.pressed = true;
                break;
            case 1:
                mc.getGameSettings().keyBindForward.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindForward);
                mc.getGameSettings().keyBindBack.pressed = true;
                break;
            case 2:
                mc.getGameSettings().keyBindBack.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindBack);
                break;
        }
    }

}
