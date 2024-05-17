package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(
        name = "Cape",
        category = Category.RENDER
)
public class Cape extends Module {

    private final ModeValue<String> capes = new ModeValue<>("Cape", new String[]{"Slack", "Rise6"});

    public Cape() {
        addSettings(capes);
    }

    @Listen
    public void onUpdate (UpdateEvent event) {
        switch (capes.getValue()) {
            case "Slack":
                mc.getPlayer().setLocationOfCape(new ResourceLocation("slack/capes/slack.png"));
            break;
            case "Rise6":
                mc.getPlayer().setLocationOfCape(new ResourceLocation("slack/capes/rise6.png"));
            break;
        }
    }

    @Override
    public void onDisable() {
        mc.getPlayer().setLocationOfCape(null);
    }
}
