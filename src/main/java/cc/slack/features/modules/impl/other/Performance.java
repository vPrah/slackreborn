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
        if (garbagevalue.getValue()) {
            System.gc();
        }
    }

    @Listen
    public void onUpdate (UpdateEvent event) {
        if (garbagevalue.getValue() && mc.getPlayer().ticksExisted % 200 == 0) {
            System.gc();
        }
        switch (performancemode.getValue()) {
            case "Simple":
                mc.getMinecraft().gameSettings.fancyGraphics = false;
                mc.getMinecraft().gameSettings.ofFastRender = true;
                break;
            case "Extreme":
                mc.getMinecraft().gameSettings.clouds = 0;
                mc.getMinecraft().gameSettings.ofCloudsHeight = 0F;
                mc.getMinecraft().gameSettings.fancyGraphics = false;
                mc.getMinecraft().gameSettings.mipmapLevels = 0;
                mc.getMinecraft().gameSettings.particleSetting = 2;
                mc.getMinecraft().gameSettings.ofDynamicLights = 0;
                mc.getMinecraft().gameSettings.ofSmoothBiomes = false;
                mc.getMinecraft().gameSettings.field_181151_V = false;
                mc.getMinecraft().gameSettings.ofTranslucentBlocks = 1;
                mc.getMinecraft().gameSettings.ofDroppedItems = 1;
                mc.getMinecraft().gameSettings.ofFastRender = true;
                break;
        }
    }
}
