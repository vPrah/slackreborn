package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.render.ColorUtil;
import cc.slack.utils.render.Render3DUtil;
import io.github.nevalackin.radbus.Listen;


import java.awt.*;

@ModuleInfo(
        name = "ChinaHat",
        category = Category.RENDER
)
public class ChinaHat extends Module {

    private final BooleanValue rgbValue = new BooleanValue("Rainbow", false);
    private final NumberValue<Integer> redValue = new NumberValue<>("Red", 0, 0, 255, 1);
    private final NumberValue<Integer> greenValue = new NumberValue<>("Green", 255, 0, 255, 1);
    private final NumberValue<Integer> blueValue = new NumberValue<>("Blue", 255, 0, 255, 1);
    private final NumberValue<Integer> alphaValue = new NumberValue<>("Alpha", 100, 0, 255, 1);

    public ChinaHat() {
        addSettings(rgbValue,redValue, greenValue, blueValue, alphaValue);
    }

    @Listen
    public void onRender (RenderEvent event) {
        if (event.getState() != RenderEvent.State.RENDER_3D) return;

        if (mc.getGameSettings().thirdPersonView != 0) {
            for (int i = 0; i < 400; ++i) {
                Render3DUtil.drawHat(mc.getPlayer(), 0.009 + i * 0.0014, mc.getTimer().elapsedPartialTicks, 12, 2.0f, 2.2f - i * 7.85E-4f - 0.03f, (!rgbValue.getValue()) ? new Color(redValue.getValue(), greenValue.getValue(), blueValue.getValue(), alphaValue.getValue()).getRGB() : ColorUtil.rainbow(-100, 1.0f, 0.47f));
            }
        }
    }

}
