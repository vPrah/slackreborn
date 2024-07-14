package cc.slack.features.modules.impl.ghost;

import cc.slack.Slack;
import cc.slack.events.impl.player.HitSlowDownEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.player.PlayerUtil;
import io.github.nevalackin.radbus.Listen;

import java.util.concurrent.TimeUnit;

@ModuleInfo(
        name = "RealLag",
        category = Category.GHOST
)
public class RealLag extends Module {

    private final NumberValue<Integer> duration = new NumberValue<>("Chance", 200, 0, 1000, 20);


    public RealLag() {
        addSettings(duration);
    }

    @Override
    public void onEnable() {
        PlayerUtil.lag(duration.getValue());
        toggle();
    }

}
