// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.player.AttackEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;
import cc.slack.utils.other.TimeUtil;
            
@ModuleInfo(
        name = "Stap",
        category = Category.GHOST
)
public class Stap extends Module {

    private int ticks;
    private final TimeUtil wtapTimer = new TimeUtil();

    @SuppressWarnings("unused")
    @Listen
    public void onAttack(AttackEvent event) {
        if (wtapTimer.hasReached(500L)) {
            wtapTimer.reset();
            ticks = 2;
        }
    }

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate(UpdateEvent event) {
        switch (ticks) {
            case 2:
                mc.gameSettings.keyBindForward.pressed = false;
                mc.gameSettings.keyBindBack.pressed = true;
                ticks--;
                break;
            case 1:
                mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
                mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
                ticks--;
                break;
        }
    }
}
