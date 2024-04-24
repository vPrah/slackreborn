package cc.slack.features.modules.impl.player;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.utils.player.BlinkUtil;


@ModuleInfo(
        name = "Blink",
        category = Category.PLAYER
)
public class Blink extends Module {


    private final BooleanValue outbound = new BooleanValue("Outbound", true);
    private final BooleanValue inbound = new BooleanValue("Inbound", false);

    public Blink() {
        super();
        addSettings(outbound, inbound);
    }

    @Override
    public void onEnable() {
        BlinkUtil.enable(inbound.getValue(), outbound.getValue());
    }

    @Override
    public void onDisable() {
        BlinkUtil.disable();
    }


}
