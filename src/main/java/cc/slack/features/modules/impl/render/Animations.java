// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;

@ModuleInfo(
        name = "Animations",
        category = Category.RENDER
)
public class Animations extends Module {

    public final ModeValue<String> blockStyle = new ModeValue<>("Block Animation", new String[]{"1.7", "1.8", "Slide", "Exhibition", "Slack"});

    public static final NumberValue<Double> animationSpeedValue = new NumberValue<>("Animation Speed", 6.0D, 0.0D, 15.0D, 1.0D);

    public Animations() {
        addSettings(blockStyle, animationSpeedValue);
    }

}
