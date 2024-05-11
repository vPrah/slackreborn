// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;

@ModuleInfo(
        name = "Animations",
        category = Category.RENDER
)
public class Animations extends Module {

    public final ModeValue<String> blockStyle = new ModeValue<>("Block Animation", new String[]{"1.8", "1.7", "Slide"});

    public Animations() {
        addSettings(blockStyle);
    }

}
