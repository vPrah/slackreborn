// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(
        name = "JumpReset",
        category = Category.GHOST
)
public class JumpReset extends Module {
    @Listen
    public void onUpdate (UpdateEvent event) {
        if (mc.getCurrentScreen() != null) return;
        if (mc.getPlayer().hurtTime >= 8) {
            mc.getGameSettings().keyBindJump.pressed = true;
        }
        if (mc.getPlayer().hurtTime >= 7) {
            mc.getGameSettings().keyBindForward.pressed = true;
        } else if (mc.getPlayer().hurtTime >= 4) {
            mc.getGameSettings().keyBindJump.pressed = false;
            mc.getGameSettings().keyBindForward.pressed = false;
        } else if (mc.getPlayer().hurtTime > 1){
            mc.getGameSettings().keyBindForward.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindForward);
            mc.getGameSettings().keyBindJump.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindJump);
        }
    }
}
