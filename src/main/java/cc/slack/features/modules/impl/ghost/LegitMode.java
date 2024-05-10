package cc.slack.features.modules.impl.ghost;

import cc.slack.Slack;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.features.modules.impl.render.ChestESP;
import cc.slack.features.modules.impl.render.ESP;
import cc.slack.features.modules.impl.render.HUD;
import cc.slack.features.modules.impl.render.TargetHUD;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

import java.io.File;

@ModuleInfo(
        name = "LegitMode",
        category = Category.GHOST
)
public class LegitMode extends Module {


    private final BooleanValue selfDestruct = new BooleanValue("Self Destruct", false);
    private final File logsDirectory = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "logs" + File.separator);


    // I need make Changing Icon to Minecraft 1.8.8 default icon

    public LegitMode() {
        addSettings(selfDestruct);
    }

    @Override
    public void onEnable() {
        Display.setTitle("Minecraft 1.8.8");
        toggleModule(HUD.class);
        toggleModule(TargetHUD.class);
        toggleModule(ESP.class);
        toggleModule(ChestESP.class);

        if (selfDestruct.getValue()) {
            this.deleteLogs();
        }
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

    private void deleteLogs() {
        if (logsDirectory.exists()) {
            File[] files = logsDirectory.listFiles();
            if (files == null)
                return;

            for (File file : files) {
                if (file.getName().endsWith("log.gz")) {
                    file.delete();
                }
            }
        }
    }
}
