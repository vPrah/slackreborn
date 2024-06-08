package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.render.ColorUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(
        name = "PointerESP",
        category = Category.RENDER
)
public class PointerESP extends Module {

    private final BooleanValue rgbValue = new BooleanValue("Rainbow", false);
    public final NumberValue<Integer> redValue = new NumberValue<>("Red", 116, 0, 255, 1);
    public final NumberValue<Integer> greenValue = new NumberValue<>("Green", 202, 0, 255, 1);
    public final NumberValue<Integer> blueValue = new NumberValue<>("Blue", 255, 0, 255, 1);
    private final NumberValue<Integer> alphaValue = new NumberValue<>("Alpha", 100, 0, 255, 1);


    public PointerESP() {
        addSettings(rgbValue, redValue, greenValue, blueValue, alphaValue);
    }

    @Listen
    public void onRender (RenderEvent event) {
        if (event.getState() == RenderEvent.State.RENDER_2D) {
            ScaledResolution scaledResolution = new ScaledResolution(mc.getMinecraft());
            int size = 100;
            double xOffset = scaledResolution.getScaledWidth() / 2F - 50.2;
            double yOffset = scaledResolution.getScaledHeight() / 2F - 49.5;
            double playerOffsetX = mc.getPlayer().posX;
            double playerOffSetZ = mc.getPlayer().posZ;


            for (Entity entity : mc.getWorld().loadedEntityList) {
                if(entity instanceof EntityPlayer && entity != mc.getPlayer()) {
                    double pos1 = (((entity.posX + (entity.posX - entity.lastTickPosX) * mc.getTimer().renderPartialTicks) - playerOffsetX) * 0.2);
                    double pos2 = (((entity.posZ + (entity.posZ - entity.lastTickPosZ) * mc.getTimer().renderPartialTicks) - playerOffSetZ) * 0.2);
                    double cos = Math.cos(mc.getPlayer().rotationYaw * (Math.PI * 2 / 360));
                    double sin = Math.sin(mc.getPlayer().rotationYaw * (Math.PI * 2 / 360));
                    double rotY = -(pos2 * cos - pos1 * sin);
                    double rotX = -(pos1 * cos + pos2 * sin);
                    double var7 = -rotX;
                    double var9 = -rotY;
                    if(MathHelper.sqrt_double(var7 * var7 + var9 * var9) < size / 2F - 4) {
                        double angle = (Math.atan2(rotY, rotX) * 180 / Math.PI);
                        double x = ((size / 2F) * Math.cos(Math.toRadians(angle))) + xOffset + size / 2F;
                        double y = ((size / 2F) * Math.sin(Math.toRadians(angle))) + yOffset + size / 2F;
                        GL11.glPushMatrix();
                        GL11.glTranslated(x,y,0);
                        GL11.glRotatef((float) angle, 0, 0, 1);
                        GL11.glScaled(1.5, 1, 1);
                        int c = (!rgbValue.getValue()) ? new Color(redValue.getValue(), greenValue.getValue(), blueValue.getValue(), alphaValue.getValue()).getRGB() : ColorUtil.rainbow(-100, 1.0f, 0.47f).getRGB();
                        drawTriAngle(0F, 0F, 2.2F, 3F, c);
                        drawTriAngle(0F, 0F, 1.5F, 3F, c);
                        drawTriAngle(0F, 0F, 1.0F, 3F, c);
                        drawTriAngle(0F, 0F, 0.5F, 3F, c);
                        drawTriAngle(0F, 0F, 2.2F, 3F, c);
                        GL11.glPopMatrix();
                    }
                }
            }
        }
    }

    public static void drawTriAngle(float cx, float cy, float r, float n, final int color){
        GL11.glPushMatrix();
        cx *= 2.0;
        cy *= 2.0;
        double b = 6.2831852 / n;
        double p = Math.cos(b);
        double s = Math.sin(b);
        r *= 2.0;
        double x = r;
        double y = 0.0;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        GlStateManager.color(0,0,0);
        GlStateManager.resetColor();
        GL11.glColor4f(new Color(color).getRed() / 255.0f, new Color(color).getGreen() / 255.0f, new Color(color).getBlue() / 255.0f, 100f);
        GL11.glBegin(2);
        int ii = 0;
        while (ii < n) {
            GL11.glVertex2d(x + cx, y + cy);
            double t = x;
            x = p * x - s * y;
            y = s * t + p * y;
            ii++;
        }
        GL11.glEnd();
        GL11.glScalef(2f, 2f, 2f);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
        GlStateManager.color(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    public static void glColor(Color color) {
        GlStateManager.color((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F, (float) color.getAlpha() / 255F);
    }

}
