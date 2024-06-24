package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;

import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;


@ModuleInfo(
        name = "Radar",
        category = Category.RENDER
)
public class Radar extends Module {

    private final NumberValue<Float> xValue = new NumberValue<>("Pos X", 8.0F, 1.0F, 900.0F, 1F);
    private final NumberValue<Float> yValue = new NumberValue<>("Pos Y", 178F, 1.0F, 900.0F, 1F);
    private final NumberValue<Float> scaleValue = new NumberValue<>("Scale", 11.5F, 1.0F, 30.0F, 0.1F);
    private final BooleanValue roundedValue = new BooleanValue("Rounded", false);

    public Radar() {
        addSettings(xValue, yValue,scaleValue,roundedValue);
    }

    @Listen
    public void onRender (RenderEvent event) {
        RenderUtil.drawRadar(xValue.getValue(), yValue.getValue(), scaleValue.getValue(), roundedValue.getValue());
    }

}
