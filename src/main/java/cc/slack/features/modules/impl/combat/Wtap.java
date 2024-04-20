package cc.slack.features.modules.impl.combat;

import cc.slack.events.impl.player.AttackEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(
        name = "Wtap",
        category = Category.COMBAT
)
public class Wtap extends Module {

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
                ticks--;
                break;
            case 1:
                mc.getGameSettings().keyBindForward.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindForward);
                ticks--;
                break;
        }
    }
}
