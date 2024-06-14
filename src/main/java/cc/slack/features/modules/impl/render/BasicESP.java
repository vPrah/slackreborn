// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.AttackUtil;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.Timer;
import net.minecraft.util.AxisAlignedBB;
import static org.lwjgl.opengl.GL11.*;


@ModuleInfo(
        name = "BasicESP",
        category = Category.RENDER
)
public class BasicESP extends Module {

    private final BooleanValue itemESP = new BooleanValue("Item ESP", true);

    private final NumberValue<Float> lineWidth = new NumberValue<>("Line Width", 1f, 1f, 5f, 0.1f);
    private final BooleanValue rotateYaw = new BooleanValue("Yaw Rotate", false);

    public BasicESP() {
        addSettings(itemESP,lineWidth, rotateYaw);
    }

    @Listen
    public void onRender(RenderEvent event) {
        if (event.getState() != RenderEvent.State.RENDER_3D) return;

        for (Entity entity : mc.getWorld().loadedEntityList) {
            if (entity.getEntityId() == mc.getPlayer().getEntityId()) continue;
            if (entity instanceof EntityItem && !itemESP.getValue()) continue;
            if (!(entity instanceof EntityLivingBase) && !(entity instanceof EntityItem)) continue;
            if (entity instanceof EntityLivingBase) if (!AttackUtil.isTarget(entity)) continue;
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
                    entityBox.minX - entity.posX - 0.05D,
                    entityBox.minY - entity.posY,
                    entityBox.minZ - entity.posZ - 0.05D,
                    entityBox.maxX - entity.posX + 0.05D,
                    entityBox.maxY - entity.posY + 0.15D,
                    entityBox.maxZ - entity.posZ + 0.05D
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
                glRotated(entity.rotationYaw, 0, 1, 0);
            }
            RenderUtil.drawSelectionBoundingBox(axisAlignedBB);
            glTranslated(x, y, z);
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
