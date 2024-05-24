package cc.slack.features.modules.impl.render;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;

@ModuleInfo(
        name = "NameProtect",
        category = Category.RENDER
)
public class NameProtect extends Module {

    public static String nameprotect;

    public static String getNameProtect() {
        return NameProtect.nameprotect;
    }

    static {
        NameProtect.nameprotect = "§cSlack User";
    }

}
