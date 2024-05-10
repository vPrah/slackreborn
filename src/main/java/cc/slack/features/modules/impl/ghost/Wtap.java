// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.player.AttackEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;
import cc.slack.utils.other.TimeUtil;

@ModuleInfo(
        name = "Wtap",
        category = Category.GHOST
)
public class Wtap extends Module {

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
