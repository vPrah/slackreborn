// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.PlayerUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(
        name = "LegitScaffold",
        category = Category.GHOST
)
public class LegitScaffold extends Module {

    private final BooleanValue onlyGround = new BooleanValue("Only Ground", true);

    private boolean shouldSneak = false;

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {
        if (mc.getCurrentScreen() != null) return;
        shouldSneak = PlayerUtil.isOverAir() && (!onlyGround.getValue() || mc.getPlayer().onGround);
        mc.getGameSettings().keyBindSneak.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindSneak) || shouldSneak;

    }
}
