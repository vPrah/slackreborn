package cc.slack.features.modules.impl.render;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;

@ModuleInfo(
        name = "NameTags",
        category = Category.RENDER
)
public class NameTags extends Module {

    public final NumberValue<Float> scale = new NumberValue<>("Scale", 2.0f, 1.0f, 5.0f, 0.2f);

}
