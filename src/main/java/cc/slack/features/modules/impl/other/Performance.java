// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.other;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.PrintUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(
        name = "Performance",
        category = Category.OTHER
)
public class Performance extends Module {

    public ModeValue<String> performancemode = new ModeValue<>("Mode", new String[]{"Simple", "Extreme"});
    public BooleanValue garbagevalue = new BooleanValue("MemoryFix", false);

    public Performance() {
        addSettings(performancemode, garbagevalue);
    }

    @Override
    public void onEnable() {
        switch (performancemode.getValue()) {
            case "Simple":
                PrintUtil.message("Performance Mode Simple is enabled successfully");
                break;
            case "Extreme":
                PrintUtil.message("Performance Mode EXTREME is enabled successfully");
                break;
        }
    }

    @Listen
    public void onUpdate (UpdateEvent event) {
        if (garbagevalue.getValue() && mc.getPlayer().ticksExisted % 200 == 0) {
            System.gc();
        }
        configureGSettings(performancemode.getValue());
    }

    private void configureGSettings(String mode) {
        GameSettings settings = mc.getMinecraft().gameSettings;
        switch (mode) {
            case "Simple":
                settings.fancyGraphics = false;
                settings.ofFastRender = true;
                break;
            case "Extreme":
                settings.clouds = 0;
                settings.ofCloudsHeight = 0F;
                settings.fancyGraphics = false;
                settings.mipmapLevels = 0;
                settings.particleSetting = 2;
                settings.ofDynamicLights = 0;
                settings.ofSmoothBiomes = false;
                settings.field_181151_V = false;
                settings.ofTranslucentBlocks = 1;
                settings.ofDroppedItems = 1;
                settings.ofFastRender = true;
                break;
        }
    }
}
