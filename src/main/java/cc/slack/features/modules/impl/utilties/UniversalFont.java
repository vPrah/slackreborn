package cc.slack.features.modules.impl.utilties;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;

@ModuleInfo(
        name = "UniversalFont",
        category = Category.UTILITIES
)
public class UniversalFont extends Module {

    public UniversalFont() {
        addSettings();
    }
}
