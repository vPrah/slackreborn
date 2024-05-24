package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.render.Render2DUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

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
        if(event.getState() != RenderEvent.State.RENDER_3D) return;

        if (mc.getGameSettings().thirdPersonView != 0) {
            for (int i = 0; i < 400; ++i) {
                drawHat(mc.getPlayer(), 0.009 + i * 0.0014, mc.getTimer().elapsedPartialTicks, 12, 2.0f, 2.2f - i * 7.85E-4f - 0.03f, new Color(redValue.getValue(),greenValue.getValue(),blueValue.getValue(), alphaValue.getValue()).getRGB());
            }
        }
    }

    public static void drawHat(final Entity entity, final double radius, final float partialTicks, final int points, final float width, final float yAdd, final int color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glDepthMask(false);
        GL11.glLineWidth(width);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2929);
        GL11.glBegin(3);
        final double x = Render2DUtil.interpolate(entity.prevPosX, entity.posX, partialTicks) - RenderManager.viewerPosX;
        final double y = Render2DUtil.interpolate(entity.prevPosY + yAdd, entity.posY + yAdd, partialTicks) - RenderManager.viewerPosY;
        final double z = Render2DUtil.interpolate(entity.prevPosZ, entity.posZ, partialTicks) - RenderManager.viewerPosZ;
        GL11.glColor4f(new Color(color).getRed() / 255.0f, new Color(color).getGreen() / 255.0f, new Color(color).getBlue() / 255.0f, 0.15f);
        for (int i = 0; i <= points; ++i) {
            GL11.glVertex3d(x + radius * Math.cos(i * 3.141592653589793 * 2.0 / points), y, z + radius * Math.sin(i * 3.141592653589793 * 2.0 / points));
        }
        GL11.glEnd();
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }

}
