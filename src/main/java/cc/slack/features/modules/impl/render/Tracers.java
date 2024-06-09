package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.render.ColorUtil;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(
        name = "Tracers",
        category = Category.RENDER
)
public class Tracers extends Module {

    private final BooleanValue clientValue = new BooleanValue("Client Theme", true);
    private final BooleanValue rgbValue = new BooleanValue("Rainbow", false);
    private final NumberValue<Integer> redValue = new NumberValue<>("Red", 0, 0, 255, 1);
    private final NumberValue<Integer> greenValue = new NumberValue<>("Green", 255, 0, 255, 1);
    private final NumberValue<Integer> blueValue = new NumberValue<>("Blue", 255, 0, 255, 1);
    private final NumberValue<Integer> alphaValue = new NumberValue<>("Alpha", 200, 0, 255, 1);

    public Tracers() {
        addSettings(clientValue,rgbValue, redValue, greenValue, blueValue, alphaValue);
    }

    @Listen
    public void onRender (RenderEvent event) {
        Color c = ColorUtil.getColor();
        if (event.getState() != RenderEvent.State.RENDER_3D) return;

        for(Entity entity : mc.getWorld().getLoadedEntityList()) {
            if(entity instanceof EntityPlayer && entity != mc.getPlayer()) {
                RenderUtil.drawTracer(entity, rgbValue.getValue(), clientValue.getValue(), redValue.getValue(), greenValue.getValue(), blueValue.getValue(), alphaValue.getValue());
            }
        }
    }

}
