package cc.slack.features.modules.impl.ghost;

import cc.slack.Slack;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.impl.render.ChestESP;
import cc.slack.features.modules.impl.render.ESP;
import cc.slack.features.modules.impl.render.HUD;
import cc.slack.features.modules.impl.render.TargetHUD;
import org.lwjgl.opengl.Display;

@ModuleInfo(
        name = "LegitMode",
        category = Category.GHOST
)
public class LegitMode extends Module {

    // I need make Changing Icon to Minecraft 1.8.8 default icon

    @Override
    public void onEnable() {
        Display.setTitle("Minecraft 1.8.8");
        toggleModule(HUD.class);
        toggleModule(TargetHUD.class);
        toggleModule(ESP.class);
        toggleModule(ChestESP.class);
    }

    @Override
    public void onDisable() {
        Display.setTitle(Slack.getInstance().getInfo().getName() + " " + Slack.getInstance().getInfo().getVersion() +  " | " + Slack.getInstance().getInfo().getType() + " Build");
    }

    private void toggleModule(Class<? extends Module> moduleClass) {
        Module module = Slack.getInstance().getModuleManager().getInstance(moduleClass);
        if (module.isToggle()) {
            module.toggle();
        }
    }
}
