// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.Timer;
import net.minecraft.util.AxisAlignedBB;
import static org.lwjgl.opengl.GL11.*;


@ModuleInfo(
        name = "ESP",
        category = Category.RENDER
)
public class ESP extends Module {

    private final NumberValue<Float> lineWidth = new NumberValue<>("Line Width", 1f, 1f, 3f, 0.1f);
    private final BooleanValue rotateYaw = new BooleanValue("Yaw Rotate", false);

    public ESP() {
        addSettings(lineWidth, rotateYaw);
    }

    @Listen
    public void onRender(RenderEvent event) {
        if (event.getState() != RenderEvent.State.RENDER_3D) return;

        for (Entity entity : mc.getWorld().loadedEntityList) {
            if (entity.getEntityId() == mc.getPlayer().getEntityId()) continue;
            final RenderManager renderManager = mc.getRenderManager();
            final Timer timer = mc.getTimer();

            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            RenderUtil.enableGlCap(GL_BLEND);
            RenderUtil.disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST);
            glDepthMask(false);

            final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks
                    - renderManager.renderPosX;
            final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks
                    - renderManager.renderPosY;
            final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks
                    - renderManager.renderPosZ;

            final AxisAlignedBB entityBox = entity.getEntityBoundingBox();
            final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                    entityBox.minX - entity.posX + x - 0.05D,
                    entityBox.minY - entity.posY + y,
                    entityBox.minZ - entity.posZ + z - 0.05D,
                    entityBox.maxX - entity.posX + x + 0.05D,
                    entityBox.maxY - entity.posY + y + 0.15D,
                    entityBox.maxZ - entity.posZ + z + 0.05D
            );

            glLineWidth(lineWidth.getValue());
            RenderUtil.enableGlCap(GL_LINE_SMOOTH);
            if (entity.hurtResistantTime > 1) {
                glColor(255, 10, 10, 95);
            } else {
                glColor(255, 255, 255, 95);
            }
            glPushMatrix();
            if (rotateYaw.getValue()) {
                glTranslated(-x, -y, -z);
                glRotated(entity.rotationYaw, 0, 1, 0);
                glTranslated(x, y, z);
            }
            RenderUtil.drawSelectionBoundingBox(axisAlignedBB);
            glPopMatrix();

            GlStateManager.resetColor();
            glDepthMask(true);
            RenderUtil.resetCaps();
        }

    }
    public static void glColor(final int red, final int green, final int blue, final int alpha) {
        GlStateManager.color(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }

}
