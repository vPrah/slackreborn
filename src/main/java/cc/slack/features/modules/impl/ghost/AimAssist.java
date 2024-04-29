// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "AimAssist",
        category = Category.GHOST
)
public class AimAssist extends Module {

    private BooleanValue lowerSens = new BooleanValue("Lower Sensitivity On Target", true);

    private float prevSens;
    private boolean wasHovering = false;

    public AimAssist() {
        addSettings(lowerSens);
    }

    @Listen
    public void onUpdate (UpdateEvent event) {
        if (lowerSens.getValue()) {
            if (mc.getMinecraft().objectMouseOver.entityHit != null) {
                if (!wasHovering) {
                    wasHovering = true;
                    prevSens = mc.getGameSettings().mouseSensitivity;
                    mc.getGameSettings().mouseSensitivity = prevSens * 0.7f;
                }
            } else {
                if (wasHovering) {
                    wasHovering = false;
                    mc.getGameSettings().mouseSensitivity = prevSens;
                }
            }
        }
    }
}
