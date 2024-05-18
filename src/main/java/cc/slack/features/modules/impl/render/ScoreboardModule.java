package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.RenderScoreboard;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.renderer.GlStateManager;

@ModuleInfo(
        name = "Scoreboard",
        category = Category.RENDER
)
public class ScoreboardModule extends Module {

    public final BooleanValue noscoreboard = new BooleanValue("No Scoreboard", false);
    private final NumberValue<Double> x = new NumberValue<>("PosX", 0.0D, -300.0D, 300.0D, 0.1D);
    private final NumberValue<Double> y = new NumberValue<>("PosY", 30.0D, -300.0D, 300.0D, 0.1D);


    public ScoreboardModule() {
        addSettings(noscoreboard, x, y);
    }

    @SuppressWarnings("unused")
    @Listen
    public void onRenderScoreboard (RenderScoreboard event) {
        if (event.isPre()) {
            GlStateManager.translate(-x.getValue(), y.getValue(), 1.0);
        }
        else {
            GlStateManager.translate(x.getValue(), -y.getValue(), 1.0);
        }
    }

}
