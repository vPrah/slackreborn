package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.render.ColorUtil;
import cc.slack.utils.render.Render2DUtil;
import cc.slack.utils.render.Render3DUtil;
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


    private final BooleanValue rgbValue = new BooleanValue("Rainbow", false);

    private final NumberValue<Integer> redValue = new NumberValue<>("Red", 0, 0, 255, 1);
    private final NumberValue<Integer> greenValue = new NumberValue<>("Green", 255, 0, 255, 1);
    private final NumberValue<Integer> blueValue = new NumberValue<>("Blue", 255, 0, 255, 1);
    private final NumberValue<Integer> alphaValue = new NumberValue<>("Alpha", 200, 0, 255, 1);

    public Tracers() {
        addSettings(rgbValue,redValue, greenValue, blueValue, alphaValue);
    }

    @Listen
    public void onRender (RenderEvent event) {
        if (event.getState() != RenderEvent.State.RENDER_3D) return;

        for(Entity entity : mc.getWorld().getLoadedEntityList()) {
            if(entity instanceof EntityPlayer && entity != mc.getPlayer()) {
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosY + entity.getEyeHeight();
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosZ;

                GL11.glPushMatrix();

                GL11.glLoadIdentity();
                mc.getEntityRenderer().orientCamera(mc.getTimer().renderPartialTicks);
                GlStateManager.disableTexture2D();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GlStateManager.disableDepth();
                GlStateManager.enableBlend();

                Render2DUtil.glColor((!rgbValue.getValue()) ? new Color(redValue.getValue(), greenValue.getValue(), blueValue.getValue(), alphaValue.getValue()).getRGB() : ColorUtil.rainbow(-100, 1.0f, 0.47f));
                GL11.glLineWidth(1.5f);

                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex3d(0, mc.getPlayer().getEyeHeight(), 0);
                GL11.glVertex3d(x, y, z);
                GL11.glEnd();

                GlStateManager.enableDepth();
                GlStateManager.disableBlend();
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GlStateManager.enableTexture2D();

                GL11.glPopMatrix();

            }
        }
    }

}
