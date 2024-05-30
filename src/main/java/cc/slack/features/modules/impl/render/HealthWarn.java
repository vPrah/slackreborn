package cc.slack.features.modules.impl.render;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "HealthWarn",
        category = Category.RENDER
)
public class HealthWarn extends Module {

    private final NumberValue<Integer> healthValue = new NumberValue<>("Health", 9, 1, 20, 1);

    boolean isHealthLow = true;

    public HealthWarn() {
        addSettings(healthValue);
    }

    @Override
    public void onEnable() {
        isHealthLow = true;
    }

    @Override
    public void onDisable() {
        isHealthLow = true;
    }

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {
        if (mc.getPlayer().getHealth() < healthValue.getValue()) {
            if (isHealthLow) {
                Slack.getInstance().getModuleManager().getInstance(HUD.class).addNotification("HealthWarn ", "YOU ARE AT LOW HP!", 4500L, Slack.NotificationStyle.WARN);
                isHealthLow = false;
            } else {
                isHealthLow = true;
            }
        }
    }

}
