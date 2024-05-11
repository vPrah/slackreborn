// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.Timer;
import net.minecraft.util.AxisAlignedBB;

import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.opengl.GL11.*;


@ModuleInfo(
        name = "ESP",
        category = Category.RENDER
)
public class ESP extends Module {

    private static final Map<String, Map<Integer, Boolean>> glCapMap = new HashMap<>();


    @Listen
    public void onRender(RenderEvent event) {
        if (event.getState() != RenderEvent.State.RENDER_3D) return;

        for (Entity entity : mc.getWorld().loadedEntityList) {
            if (entity.getEntityId() == mc.getPlayer().getEntityId()) continue;
            final RenderManager renderManager = mc.getRenderManager();
            final Timer timer = mc.getTimer();

            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            enableGlCap(GL_BLEND);
            disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST);
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

            glLineWidth(1F);
            enableGlCap(GL_LINE_SMOOTH);
            if (entity.hurtResistantTime > 1) {
                glColor(255, 10, 10, 95);
            } else {
                glColor(255, 255, 255, 95);
            }
            drawSelectionBoundingBox(axisAlignedBB);

            GlStateManager.resetColor();
            glDepthMask(true);
            resetCaps();
        }

    }

    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX);

        // Lower Rectangle
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();

        // Upper Rectangle
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();

        // Upper Rectangle
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();

        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();

        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();

        tessellator.draw();
    }

    public static void enableGlCap(final int cap, final String scale) {
        setGlCap(cap, true, scale);
    }

    public static void enableGlCap(final int cap) {
        enableGlCap(cap, "COMMON");
    }


    public static void disableGlCap(final int... caps) {
        for(int cap : caps) {
            setGlCap(cap, false, "COMMON");
        }
    }

    public static void setGlCap(final int cap, final boolean state, final String scale) {
        if(!glCapMap.containsKey(scale)) {
            glCapMap.put(scale, new HashMap<>());
        }
        glCapMap.get(scale).put(cap, glGetBoolean(cap));
        setGlState(cap, state);
    }

    public static void setGlState(final int cap, final boolean state) {
        if (state)
            glEnable(cap);
        else
            glDisable(cap);
    }

    public static void glColor(final int red, final int green, final int blue, final int alpha) {
        GlStateManager.color(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }

    public static void resetCaps(final String scale) {
        if(!glCapMap.containsKey(scale)) {
            return;
        }
        Map<Integer, Boolean> map = glCapMap.get(scale);
        map.forEach(ESP::setGlState);
        map.clear();
    }

    public static void resetCaps() {
        resetCaps("COMMON");
    }

}
