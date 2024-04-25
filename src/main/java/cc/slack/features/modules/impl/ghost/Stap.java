package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.player.AttackEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(
        name = "Stap",
        category = Category.GHOST
)
public class Stap extends Module {

    private int ticks;

    @Listen
    public void onAttack(AttackEvent event) {
        ticks = 2;
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        switch (ticks) {
            case 2:
                mc.getGameSettings().keyBindForward.pressed = false;
                mc.getGameSettings().keyBindBack.pressed = false;
                ticks--;
                break;
            case 1:
                mc.getGameSettings().keyBindForward.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindForward);
                mc.getGameSettings().keyBindBack.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindBack);
                ticks--;
                break;
        }
    }
}
