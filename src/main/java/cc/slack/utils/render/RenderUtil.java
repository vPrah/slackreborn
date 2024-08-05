package cc.slack.utils.render;

import cc.slack.start.Slack;
import cc.slack.features.modules.impl.render.PointerESP;
import cc.slack.features.modules.impl.render.Tracers;
import cc.slack.utils.client.IMinecraft;
import cc.slack.utils.other.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public final class RenderUtil implements IMinecraft {

    private static final Map<String, Map<Integer, Boolean>> glCapMap = new HashMap<>();

    public static RenderItem renderItem = mc.getRenderItem();
    public static long tick;
    public static double rotation;
    public static Random random = new Random();
    private static final Frustum frustrum = new Frustum();
    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);


    public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
    }


    public static Vector3d project(double x, double y, double z) {
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(2982, modelview);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        if (GLU.gluProject((float)x, (float)y, (float)z, modelview, projection, viewport, vector)) {
            return new Vector3d(vector.get(0) / (float)new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor(), ((float) Display.getHeight() - vector.get(1)) / (float)new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor(), vector.get(2));
        }
        return null;
    }

    public static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }


    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX);

        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();

        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();

        tessellator.draw();
    }

    public static void drawFilledBoundingBox(final AxisAlignedBB axisAlignedBB) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();

        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();

        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();

        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();

        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();

        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
    }

    public static void renderZoom (float fov, float smoothspeed, boolean slowsensitivity) {
        mc.gameSettings.smoothCamera = Keyboard.isKeyDown(Keyboard.KEY_C) && slowsensitivity;

        if (mc.gameSettings.fovSetting >= fov) {
            mc.gameSettings.fovSetting = fov;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_C) && mc.gameSettings.fovSetting > fov) {
            mc.gameSettings.fovSetting = fov;
        }
        if (mc.gameSettings.fovSetting <= 25F) {
            mc.gameSettings.fovSetting = 25F;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_C) && mc.gameSettings.fovSetting == 25F) {
            return;
        }

        float zoom = 0F;
        zoom += (0.0075F * smoothspeed * (int) ((Sys.getTime() * 1000) / Sys.getTimerResolution() - Minecraft.getSystemTime())  * (Keyboard.isKeyDown(Keyboard.KEY_C) ? 1F : -1F));
        zoom = Math.max(0F, Math.min(zoom, 1F));

        if (Keyboard.isKeyDown(Keyboard.KEY_C) || mc.gameSettings.fovSetting != fov) {
            mc.gameSettings.fovSetting -= ((fov - 25) * zoom);
        }
        if (mc.gameSettings.fovSetting <= 25F) {
            mc.gameSettings.fovSetting = 25F;
        }
    }

    public static void drawRadar (float x, float y, float scale, boolean rounded) {
        Color ct = ColorUtil.getColor();
        float width = scale * 10;
        float height = scale * 10;
        if (!rounded) {
            Gui.drawRect(x, y, (x + width), (y + height), (new Color(-1121968096, true)).getRGB());
            Gui.drawRect((x + 1.0F), (double)(y + height) - 1.5, (x + width - 1.0F), (y + height - 1.0F), (ct).getRGB());
        } else {
            RenderUtil.drawRoundedRect(x, y, (x + width), (y + height), 8F, (new Color(-1121968096, true)).getRGB());
            Gui.drawRect((x + 5.0F), (double)(y + height) - 1.5, (x + width - 5.0F), (y + height - 1.0F), (ct).getRGB());
        }
        float centerX = x + width / 2.0F;
        float centerY = y + height / 2.0F;
        Iterator var8 = mc.theWorld.getLoadedEntityList().iterator();

        while(var8.hasNext()) {
            Entity entity = (Entity)var8.next();
            if (entity != mc.thePlayer && entity instanceof EntityPlayer) {
                float entYaw = MathUtil.getRotations(entity.posX, entity.posY, entity.posZ)[0];
                float angleDiff = Math.abs(entYaw - (mc.thePlayer.rotationYaw - 36000.0F)) - 180.0F;
                double diffRadians = Math.toRadians(angleDiff);
                double dx = mc.thePlayer.posX - entity.posX;
                double dy = mc.thePlayer.posZ - entity.posZ;
                double dist = Math.hypot(dx, dy);
                double entx = MathHelper.clamp_double((double)centerX - Math.sin(diffRadians) * dist, (x + 2.0F), (x + width - 3.0F));
                double enty = MathHelper.clamp_double((double)centerY + Math.cos(diffRadians) * dist, (y + 2.0F), (y + height - 3.0F));
                Gui.drawRect(entx, enty, entx + 1.0, enty + 1.0, -1);
                if (Slack.getInstance().getFriendManager().isTarget((EntityLivingBase)entity)) {
                    Gui.drawRect(entx + 0.5 - 0.5, enty + 0.5 - 2.0, entx + 0.5 + 0.5, enty + 0.5 + 2.0, (new Color(-4768965, true)).getRGB());
                    Gui.drawRect(entx + 0.5 - 2.0, enty + 0.5 - 0.5, entx + 0.5 + 2.0, enty + 0.5 + 0.5, (new Color(-4768965, true)).getRGB());
                }

                if (Slack.getInstance().getFriendManager().isFriend(entity)) {
                    Gui.drawRect(entx + 0.5 - 0.5, enty + 0.5 - 2.0, entx + 0.5 + 0.5, enty + 0.5 + 2.0, (new Color(-7798888, true)).getRGB());
                    Gui.drawRect(entx + 0.5 - 2.0, enty + 0.5 - 0.5, entx + 0.5 + 2.0, enty + 0.5 + 0.5, (new Color(-7798888, true)).getRGB());
                }
            }
        }

        Gui.drawRect((double)centerX - 0.5, (centerY - 3.0F), (double)centerX + 0.5, (centerY + 3.0F), (new Color(1644167167, true)).getRGB());
        Gui.drawRect((centerX - 3.0F), (double)centerY - 0.5, (centerX + 3.0F), (double)centerY + 0.5, (new Color(1644167167, true)).getRGB());
    }

    public static void drawTNTExplosionRange (double damage) {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityTNTPrimed) {
                EntityTNTPrimed tnt = (EntityTNTPrimed) entity;
                final double posX = tnt.posX - mc.getRenderManager().renderPosX;
                final double posY = tnt.posY - mc.getRenderManager().renderPosY;
                final double posZ = tnt.posZ - mc.getRenderManager().renderPosZ;

                GL11.glPushMatrix();
                GL11.glTranslated(posX, posY, posZ);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4d(1, 0, 0, 0.2);

                Sphere sphere = new Sphere();
                sphere.setDrawStyle(GLU.GLU_FILL);
                sphere.draw(4 * 2, 15, 15);
                float f3 = 4 * 2.0F;
                Vec3 vec3 = new Vec3(tnt.posX, tnt.posY, tnt.posZ);
                if (!mc.thePlayer.isImmuneToExplosions()) {
                    double d12 = mc.thePlayer.getDistance(tnt.posX, tnt.posY, tnt.posZ) / (double) f3;
                    if (d12 <= 1.0D) {
                        double d5 = mc.thePlayer.posX - tnt.posX;
                        double d7 = mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() - tnt.posY;
                        double d9 = mc.thePlayer.posZ - tnt.posY;
                        double d13 = MathHelper.sqrt_double(d5 * d5 + d7 * d7 + d9 * d9);
                        if (d13 != 0) {
                            double d14 = mc.getWorld().getBlockDensity(vec3, mc.thePlayer.getEntityBoundingBox());
                            double d10 = (1.0D - d12) * d14;
                            damage += (float) ((int) ((d10 * d10 + d10) / 2.0D * 8.0D * (double) f3 + 1.0D));
                        }
                    }
                }

                GL11.glColor4d(1, 0, 0, 1);

                Sphere lines = new Sphere();
                lines.setDrawStyle(GLU.GLU_LINE);
                lines.draw(4 * 2 + 0.1F, 15, 15);

                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }
    }

    public static void drawTracer(Entity entity, int red, int green, int blue, int alpha) {
        Color c = ColorUtil.getColor();
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY + entity.getEyeHeight();
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

        GL11.glPushMatrix();

        GL11.glLoadIdentity();
        mc.getEntityRenderer().orientCamera(mc.timer.renderPartialTicks);
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        if (Slack.getInstance().getModuleManager().getInstance(Tracers.class).colormodes.getValue().equals("Client Theme")) {
            RenderUtil.glColor(c.getRGB());
        } else {
            RenderUtil.glColor((!Slack.getInstance().getModuleManager().getInstance(Tracers.class).colormodes.getValue().equals("Rainbow")) ? new Color(red, green, blue, alpha).getRGB() : ColorUtil.rainbow(-100, 1.0f, 0.47f).getRGB());
        }
        GL11.glLineWidth(1.5f);

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(0, mc.thePlayer.getEyeHeight(), 0);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();

        GL11.glPopMatrix();
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
        final double x = interpolate(entity.prevPosX, entity.posX, partialTicks) - RenderManager.viewerPosX;
        final double y = interpolate(entity.prevPosY + yAdd, entity.posY + yAdd, partialTicks) - RenderManager.viewerPosY;
        final double z = interpolate(entity.prevPosZ, entity.posZ, partialTicks) - RenderManager.viewerPosZ;
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

    public static void drawBlock(final BlockPos pos, Color c, Float width) {
        final Block block = mc.theWorld.getBlockState(pos).getBlock();
        final RenderManager renderManager = mc.getRenderManager();
        mc.getRenderManager();
        final double x = pos.getX() - renderManager.getRenderPosX();
        mc.getRenderManager();
        final double y = pos.getY() - renderManager.getRenderPosY();
        mc.getRenderManager();
        final double z = pos.getZ() - renderManager.getRenderPosZ();
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        glColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).getRGB());
        final double minX = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0 : block.getBlockBoundsMinX();
        final double minY = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0 : block.getBlockBoundsMinY();
        final double minZ = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0 : block.getBlockBoundsMinZ();
        drawSelectionBoundingBox(new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
        glColor(new Color(0, 0, 0).getRGB());
        GL11.glLineWidth(width);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawRenderItemPhysics(Entity par1Entity, double x, double y, double z, float par8, float par9) {
        EntityItem item;
        ItemStack itemstack;
        rotation = (double) (System.nanoTime() - tick) / 3000000.0;
        if (!mc.inGameHasFocus) {
            rotation = 0.0;
        }
        if ((itemstack = (item = (EntityItem)par1Entity).getEntityItem()).getItem() != null) {
            random.setSeed(187L);
            boolean flag = false;
            if (TextureMap.locationBlocksTexture != null) {
                mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
                flag = true;
            }
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            IBakedModel ibakedmodel = renderItem.getItemModelMesher().getItemModel(itemstack);
            int i = func_177077_a(item, x, y, z, par9, ibakedmodel);
            BlockPos pos = new BlockPos(item);
            if (item.rotationPitch > 360.0f) {
                item.rotationPitch = 0.0f;
            }
            if (!(item == null || Double.isNaN(item.getAge()) || Double.isNaN(item.getAir()) || Double.isNaN(item.getEntityId()) || item.getPosition() == null)) {
                if (item.onGround) {
                    if (item.rotationPitch != 0.0f && item.rotationPitch != 90.0f && item.rotationPitch != 180.0f && item.rotationPitch != 270.0f) {
                        double Abstand0 = formPositiv(item.rotationPitch);
                        double Abstand90 = formPositiv(item.rotationPitch - 90.0f);
                        double Abstand180 = formPositiv(item.rotationPitch - 180.0f);
                        double Abstand270 = formPositiv(item.rotationPitch - 270.0f);
                        if (Abstand0 <= Abstand90 && Abstand0 <= Abstand180 && Abstand0 <= Abstand270) {
                            if (item.rotationPitch < 0.0f) {
                                EntityItem e1 = item;
                                e1.rotationPitch = (float)((double)e1.rotationPitch + rotation);
                            } else {
                                EntityItem e2 = item;
                                e2.rotationPitch = (float)((double)e2.rotationPitch - rotation);
                            }
                        }
                        if (Abstand90 < Abstand0 && Abstand90 <= Abstand180 && Abstand90 <= Abstand270) {
                            if (item.rotationPitch - 90.0f < 0.0f) {
                                EntityItem e3 = item;
                                e3.rotationPitch = (float)((double)e3.rotationPitch + rotation);
                            } else {
                                EntityItem e4 = item;
                                e4.rotationPitch = (float)((double)e4.rotationPitch - rotation);
                            }
                        }
                        if (Abstand180 < Abstand90 && Abstand180 < Abstand0 && Abstand180 <= Abstand270) {
                            if (item.rotationPitch - 180.0f < 0.0f) {
                                EntityItem e5 = item;
                                e5.rotationPitch = (float)((double)e5.rotationPitch + rotation);
                            } else {
                                EntityItem e6 = item;
                                e6.rotationPitch = (float)((double)e6.rotationPitch - rotation);
                            }
                        }
                        if (Abstand270 < Abstand90 && Abstand270 < Abstand180 && Abstand270 < Abstand0) {
                            if (item.rotationPitch - 270.0f < 0.0f) {
                                EntityItem e7 = item;
                                e7.rotationPitch = (float)((double)e7.rotationPitch + rotation);
                            } else {
                                EntityItem e8 = item;
                                e8.rotationPitch = (float)((double)e8.rotationPitch - rotation);
                            }
                        }
                    }
                } else {
                    BlockPos posUp = new BlockPos(item);
                    posUp.add(0, 1, 0);
                    Material m1 = item.worldObj.getBlockState(posUp).getBlock().getMaterial();
                    Material m2 = item.worldObj.getBlockState(pos).getBlock().getMaterial();
                    boolean m3 = item.isInsideOfMaterial(Material.water);
                    boolean m4 = item.inWater;
                    if (m3 | m1 == Material.water | m2 == Material.water | m4) {
                        EntityItem tmp748_746 = item;
                        tmp748_746.rotationPitch = (float)((double)tmp748_746.rotationPitch + rotation / 4.0);
                    } else {
                        EntityItem tmp770_768 = item;
                        tmp770_768.rotationPitch = (float)((double)tmp770_768.rotationPitch + rotation * 2.0);
                    }
                }
            }
            GL11.glRotatef(item.rotationYaw, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef((item.rotationPitch + 90.0f), 1.0f, 0.0f, 0.0f);
            int j = 0;
            while (j < i) {
                if (ibakedmodel.isAmbientOcclusion()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.5f, 0.5f, 0.5f);
                    renderItem.renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.pushMatrix();
                    if (j > 0 && shouldSpreadItems()) {
                        GlStateManager.translate(0.0f, 0.0f, 0.046875f * (float)j);
                    }
                    renderItem.renderItem(itemstack, ibakedmodel);
                    if (!shouldSpreadItems()) {
                        GlStateManager.translate(0.0f, 0.0f, 0.046875f);
                    }
                    GlStateManager.popMatrix();
                }
                ++j;
            }
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            if (flag) {
                mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
            }
        }
    }

    public static boolean shouldSpreadItems() {
        return true;
    }

    public static int func_177077_a(EntityItem item, double x, double y, double z, float p_177077_8_, IBakedModel p_177077_9_) {
        ItemStack itemstack = item.getEntityItem();
        Item item2 = itemstack.getItem();
        if (item2 == null) {
            return 0;
        }
        boolean flag = p_177077_9_.isAmbientOcclusion();
        int i = func_177078_a(itemstack);
        float f1 = 0.25f;
        float f2 = 0.0f;
        GlStateManager.translate((float)x, (float)y + f2 + 0.25f, (float)z);
        float f3 = 0.0f;
        if (flag || mc.getRenderManager().renderEngine != null && mc.gameSettings.fancyGraphics) {
            GlStateManager.rotate(f3, 0.0f, 1.0f, 0.0f);
        }
        if (!flag) {
            f3 = -0.0f * (float)(i - 1) * 0.5f;
            float f4 = -0.0f * (float)(i - 1) * 0.5f;
            float f5 = -0.046875f * (float)(i - 1) * 0.5f;
            GlStateManager.translate(f3, f4, f5);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        return i;
    }

    public static int func_177078_a(ItemStack stack) {
        int b0 = 1;
        if (stack.animationsToGo > 48) {
            b0 = 5;
        } else if (stack.animationsToGo > 32) {
            b0 = 4;
        } else if (stack.animationsToGo > 16) {
            b0 = 3;
        } else if (stack.animationsToGo > 1) {
            b0 = 2;
        }
        return b0;
    }

    public static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255.0f;
        final float red = (hex >> 16 & 0xFF) / 255.0f;
        final float green = (hex >> 8 & 0xFF) / 255.0f;
        final float blue = (hex & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static double formPositiv(float rotationPitch) {
        if (rotationPitch > 0.0f) {
            return rotationPitch;
        }
        return - rotationPitch;
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
        map.forEach(RenderUtil::setGlState);
        map.clear();
    }

    public static void drawRect(int x, int y, int x2, int y2, int color) {
        Gui.drawRect(x, y, x2, y2, color);
    }


    public static void drawRoundedRect(double x, double y, double x1, double y1, double radius, int color) {
        int i;
        GL11.glPushMatrix();
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        x *= 2.0;
        y *= 2.0;
        x1 *= 2.0;
        y1 *= 2.0;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        GlStateManager.color(r, g, b, a);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        for (i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin((double)i * Math.PI / 180.0) * radius * -1.0, y + radius + Math.cos((double)i * Math.PI / 180.0) * radius * -1.0);
        }
        for (i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin((double)i * Math.PI / 180.0) * radius * -1.0, y1 - radius + Math.cos((double)i * Math.PI / 180.0) * radius * -1.0);
        }
        for (i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x1 - radius + Math.sin((double)i * Math.PI / 180.0) * radius, y1 - radius + Math.cos((double)i * Math.PI / 180.0) * radius);
        }
        for (i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x1 - radius + Math.sin((double)i * Math.PI / 180.0) * radius, y + radius + Math.cos((double)i * Math.PI / 180.0) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glPopAttrib();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();

        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawRoundedRectBorder(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, float radius, int color, float width) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        float z;
        if (paramXStart > paramXEnd) {
            z = paramXStart;
            paramXStart = paramXEnd;
            paramXEnd = z;
        }

        if (paramYStart > paramYEnd) {
            z = paramYStart;
            paramYStart = paramYEnd;
            paramYEnd = z;
        }

        double x1 = (paramXStart + radius);
        double y1 = (paramYStart + radius);
        double x2 = (paramXEnd - radius);
        double y2 = (paramYEnd - radius);

        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(width);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        glColor4f(red, green, blue, alpha);

        double degree = Math.PI / 180;
        for (double i = 0; i <= 90; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        for (double i = 90; i <= 180; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 180; i <= 270; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 270; i <= 360; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        glVertex2d(x2, y2 + radius);

        glEnd();
        glColor4f(1, 1, 1, 1);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public static double interpolate(final double old, final double now, final float partialTicks) {
        return old + (now - old) * partialTicks;
    }

    public static double interpolate(double now, double old, double scale) {

        return old + (now - old) * scale;
    }

    public static void drawArmor(EntityPlayer player, int x, int y, float size) {
        if (player.inventory.armorInventory.length > 0) {
            List<ItemStack> items = new ArrayList<>();
            if (player.getHeldItem() != null) {
                items.add(player.getHeldItem());
            }
            for (int index = 3; index >= 0; index--) {
                ItemStack stack = player.inventory.armorInventory[index];
                if (stack != null) {
                    items.add(stack);
                }
            }
            int armorX = x - ((items.size() * 18) / 2);
            for (ItemStack stack : items) {
                GlStateManager.pushMatrix();
                GlStateManager.enableLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, armorX, y);
                mc.getRenderItem().renderItemOverlays(mc.getFontRenderer(), stack, armorX, y);
                GlStateManager.disableLighting();
                GlStateManager.popMatrix();
                GlStateManager.disableDepth();
                NBTTagList enchants = stack.getEnchantmentTagList();
                GlStateManager.pushMatrix();
                GlStateManager.scale(size, size, size);
                if (stack.getItem() == Items.golden_apple && stack.getMetadata() == 1) {
                    mc.getFontRenderer().drawString("op", armorX / size, y / size, 0xFFFF0000, true);
                }
                Enchantment[] important = new Enchantment[]{Enchantment.protection, Enchantment.sharpness, Enchantment.fireAspect, Enchantment.efficiency, Enchantment.power, Enchantment.flame};
                if (enchants != null) {
                    int ency = y + 8;
                    for (int index = 0; index < enchants.tagCount(); ++index) {
                        short id = enchants.getCompoundTagAt(index).getShort("id");
                        short level = enchants.getCompoundTagAt(index).getShort("lvl");
                        Enchantment enc = Enchantment.getEnchantmentById(id);
                        for (Enchantment importantEnchantment : important) {
                            if (enc == importantEnchantment) {
                                String encName = enc.getTranslatedName(level).substring(0, 1).toLowerCase();
                                if (level > 99) encName = encName + "99+";
                                else encName = encName + level;
                                mc.getFontRenderer().drawString(encName, armorX / size + 4, ency / size, 0xDDD1E6, true);
                                ency -= 5;
                                break;
                            }
                        }
                    }
                }
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
                armorX += 18;
            }
        }
    }

    public static Vector4d getProjectedEntity(EntityPlayer ent, double partialTicks) {
        return getProjectedEntity(ent, partialTicks, 1);
    }

    public static Vector4d getProjectedEntity(EntityPlayer ent, double partialTicks, double heightPercent) {
        double posX = RenderUtil.interpolate(ent.posX, ent.lastTickPosX, partialTicks);
        double posY = RenderUtil.interpolate(ent.posY, ent.lastTickPosY, partialTicks);
        double posZ = RenderUtil.interpolate(ent.posZ, ent.lastTickPosZ, partialTicks);
        double width = ent.width / 1.5;
        double height = (ent.height + (ent.isSneaking() ? -0.3 : 0.2)) * heightPercent;
        AxisAlignedBB aabb = new AxisAlignedBB(posX - width, posY, posZ - width, posX + width, posY + height, posZ + width);
        List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
        mc.getEntityRenderer().setupCameraTransform((float) partialTicks, 0);
        Vector4d position = null;
        for (Vector3d vector : vectors) {
            vector = RenderUtil.project(vector.field_181059_a - mc.getRenderManager().viewerPosX, vector.field_181060_b - mc.getRenderManager().viewerPosY, vector.field_181061_c - mc.getRenderManager().viewerPosZ);
            if (vector != null && vector.field_181061_c >= 0.0 && vector.field_181061_c < 1.0) {
                if (position == null) {
                    position = new Vector4d(vector.field_181059_a, vector.field_181060_b, vector.field_181061_c, 0.0);
                }
                position.x = Math.min(vector.field_181059_a, position.x);
                position.y = Math.min(vector.field_181060_b, position.y);
                position.z = Math.max(vector.field_181059_a, position.z);
                position.w = Math.max(vector.field_181060_b, position.w);
            }
        }
        return position;
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

    public static int toRGBAHex(float r, float g, float b, float a) {
        return ((int)(a * 255.0F) & 255) << 24 | ((int)(r * 255.0F) & 255) << 16 | ((int)(g * 255.0F) & 255) << 8 | (int)(b * 255.0F) & 255;
    }

    public static float[] getColorForTileEntity() {
        Color color = ColorUtil.getColor();
        return new float[]{(float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), 200.0F};
    }

    public static void renderBed(BlockPos[] array) {
        Color ct = ColorUtil.getColor();
        double deltaX = (double) array[0].getX() - mc.getRenderManager().viewerPosX;
        double deltaY = (double) array[0].getY() - mc.getRenderManager().viewerPosY;
        double deltaZ = (double) array[0].getZ() - mc.getRenderManager().viewerPosZ;

        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4d(ct.getRed(), ct.getGreen(), ct.getBlue(), 160);

        AxisAlignedBB axisAlignedBB;
        if (array[0].getX() != array[1].getX()) {
            if (array[0].getX() > array[1].getX()) {
                axisAlignedBB = new AxisAlignedBB(deltaX - 1.0,deltaY, deltaZ, deltaX + 1.0, deltaY + 0.5625, deltaZ + 1.0);
            } else {
                axisAlignedBB = new AxisAlignedBB(deltaX, deltaY, deltaZ, deltaX + 2.0, deltaY + 0.5625, deltaZ + 1.0);
            }
        } else if (array[0].getZ() > array[1].getZ()) {
            axisAlignedBB = new AxisAlignedBB(deltaX, deltaY, deltaZ - 1.0, deltaX + 1.0, deltaY + 0.5625, deltaZ + 1.0);
        } else {
            axisAlignedBB = new AxisAlignedBB(deltaX, deltaY, deltaZ, deltaX + 1.0, deltaY + 0.5625, deltaZ + 2.0);
        }

        float[] colors = getColorForTileEntity();
        RenderHelper.drawCompleteBoxFilled(axisAlignedBB, 1.0F, toRGBAHex(colors[0] / 255.0F, colors[1] / 255.0F, colors[2] / 255.0F, 0.2F));

        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }

    public static void drawFilledBlock(BlockPos blockPos, int color) {
        double deltaX = (double) blockPos.getX() - mc.getRenderManager().viewerPosX;
        double deltaY = (double) blockPos.getY() - mc.getRenderManager().viewerPosY;
        double deltaZ = (double) blockPos.getZ() - mc.getRenderManager().viewerPosZ;

        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        RenderHelper.drawCompleteBoxFilled(new AxisAlignedBB(deltaX, deltaY, deltaZ, deltaX + 1.0, deltaY + 1.0, deltaZ + 1.0), 1.0F, color);

        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);

    }

    public static void enable(final boolean disableDepth) {
        if (disableDepth) {
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
        }
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0f);
    }

    public static void disable(final boolean enableDepth) {
        if (enableDepth) {
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawProjectiles(int color) {
        boolean isBow = false;
        float pitchDifference = 0.0f;
        float motionFactor = 1.5f;
        float motionSlowdown = 0.99f;
        if (mc.thePlayer.getHeldItem() != null) {
            final Item item = mc.thePlayer.getHeldItem().getItem();
            float gravity;
            float size;
            if (item instanceof ItemBow) {
                isBow = true;
                gravity = 0.05f;
                size = 0.3f;
                float power = mc.thePlayer.getItemInUseCount() / 20.0f;
                power = (power * power + power * 2.0f) / 3.0f;
                if (power < 0.1) {
                    return;
                }
                if (power > 1.0f) {
                    power = 1.0f;
                }
                motionFactor = power * 3.0f;
            }
            else if (item instanceof ItemFishingRod) {
                gravity = 0.04f;
                size = 0.25f;
                motionSlowdown = 0.92f;
            }
            else if (mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion && ItemPotion.isSplash(mc.thePlayer.getHeldItem().getItemDamage())) {
                gravity = 0.05f;
                size = 0.25f;
                pitchDifference = -20.0f;
                motionFactor = 0.5f;
            }
            else {
                if (!(item instanceof ItemSnowball) && !(item instanceof ItemEnderPearl) && !(item instanceof ItemEgg)) {
                    return;
                }
                gravity = 0.03f;
                size = 0.25f;
            }
            double posX = mc.getRenderManager().renderPosX - MathHelper.cos(mc.thePlayer.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
            double posY = mc.getRenderManager().renderPosY + mc.thePlayer.getEyeHeight() - 0.10000000149011612;
            double posZ = mc.getRenderManager().renderPosZ - MathHelper.sin(mc.thePlayer.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
            double motionX = -MathHelper.sin(mc.thePlayer.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0f * 3.1415927f) * (isBow ? 1.0 : 0.4);
            double motionY = -MathHelper.sin((mc.thePlayer.rotationPitch + pitchDifference) / 180.0f * 3.1415927f) * (isBow ? 1.0 : 0.4);
            double motionZ = MathHelper.cos(mc.thePlayer.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0f * 3.1415927f) * (isBow ? 1.0 : 0.4);
            final float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= distance;
            motionY /= distance;
            motionZ /= distance;
            motionX *= motionFactor;
            motionY *= motionFactor;
            motionZ *= motionFactor;
            MovingObjectPosition landingPosition = null;
            boolean hasLanded = false;
            boolean hitEntity = false;
            enable(true);
            glColor(color);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            final int limit = 0;
            while (!hasLanded && posY > 0.0) {
                Vec3 posBefore = new Vec3(posX, posY, posZ);
                Vec3 posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                landingPosition = mc.theWorld.rayTraceBlocks(posBefore, posAfter, false, true, false);
                posBefore = new Vec3(posX, posY, posZ);
                posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                if (landingPosition != null) {
                    hasLanded = true;
                    posAfter = new Vec3(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
                }
                final AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
                final List<Entity> entityList = getEntitiesWithinAABB(arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
                for (int i = 0; i < entityList.size(); ++i) {
                    final Entity possibleEntity = entityList.get(i);
                    if (possibleEntity.canBeCollidedWith() && possibleEntity != mc.thePlayer) {
                        final AxisAlignedBB possibleEntityBoundingBox = possibleEntity.getEntityBoundingBox().expand(size, size, size);
                        final MovingObjectPosition possibleEntityLanding = possibleEntityBoundingBox.calculateIntercept(posBefore, posAfter);
                        if (possibleEntityLanding != null) {
                            hitEntity = true;
                            hasLanded = true;
                            landingPosition = possibleEntityLanding;
                        }
                    }
                }
                posX += motionX;
                posY += motionY;
                posZ += motionZ;
                final BlockPos var18 = new BlockPos(posX, posY, posZ);
                final Block var19 = mc.theWorld.getBlockState(var18).getBlock();
                if (var19.getMaterial() == Material.water) {
                    motionX *= 0.6;
                    motionY *= 0.6;
                    motionZ *= 0.6;
                }
                else {
                    motionX *= motionSlowdown;
                    motionY *= motionSlowdown;
                    motionZ *= motionSlowdown;
                }
                motionY -= gravity;
                GL11.glVertex3d(posX - mc.getRenderManager().renderPosX, posY - mc.getRenderManager().renderPosY, posZ - mc.getRenderManager().renderPosZ);
            }
            GL11.glEnd();
            GL11.glPushMatrix();
            GL11.glTranslated(posX - mc.getRenderManager().renderPosX, posY - mc.getRenderManager().renderPosY, posZ - mc.getRenderManager().renderPosZ);
            if (landingPosition != null) {
                switch (landingPosition.sideHit.getAxis().ordinal()) {
                    case 0: {
                        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                        break;
                    }
                    case 2: {
                        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                        break;
                    }
                }
                if (hitEntity) {
                    glColor(0xFFFF0000);
                }
            }
            renderPoint();
            GL11.glPopMatrix();
            disable(true);
        }
    }

    private static void renderPoint() {
        GL11.glBegin(1);
        GL11.glVertex3d(-0.5, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, -0.5);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.5, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.5);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glEnd();
        final Cylinder c = new Cylinder();
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        c.draw(0.5f, 0.5f, 0.1f, 24, 1);
    }

    private static List<Entity> getEntitiesWithinAABB(final AxisAlignedBB axisalignedBB) {
        final List<Entity> list = new ArrayList<Entity>();
        final int chunkMinX = MathHelper.floor_double((axisalignedBB.minX - 2.0) / 16.0);
        final int chunkMaxX = MathHelper.floor_double((axisalignedBB.maxX + 2.0) / 16.0);
        final int chunkMinZ = MathHelper.floor_double((axisalignedBB.minZ - 2.0) / 16.0);
        final int chunkMaxZ = MathHelper.floor_double((axisalignedBB.maxZ + 2.0) / 16.0);
        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                mc.theWorld.getChunkFromChunkCoords(x, z).getEntitiesWithinAABBForEntity(mc.thePlayer, axisalignedBB, list, null);
            }
        }
        return list;
    }

    public static void RenderPointerESP() {
        int c;
        Color ct = ColorUtil.getColor();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int size = 100;
        double xOffset = scaledResolution.getScaledWidth() / 2F - 50.2;
        double yOffset = scaledResolution.getScaledHeight() / 2F - 49.5;
        double playerOffsetX = mc.thePlayer.posX;
        double playerOffSetZ = mc.thePlayer.posZ;


        for (Entity entity : mc.theWorld.loadedEntityList) {
            if(entity instanceof EntityPlayer && entity != mc.thePlayer) {
                double pos1 = (((entity.posX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks) - playerOffsetX) * 0.2);
                double pos2 = (((entity.posZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks) - playerOffSetZ) * 0.2);
                double cos = Math.cos(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                double sin = Math.sin(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
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
                    if (Slack.getInstance().getModuleManager().getInstance(PointerESP.class).colormodes.getValue().equals("Client Theme")) {
                        c = ct.getRGB();
                    } else {
                        c = (!(Slack.getInstance().getModuleManager().getInstance(PointerESP.class).colormodes.getValue().equals("Rainbow")) ? new Color(Slack.getInstance().getModuleManager().getInstance(PointerESP.class).redValue.getValue(), Slack.getInstance().getModuleManager().getInstance(PointerESP.class).greenValue.getValue(), Slack.getInstance().getModuleManager().getInstance(PointerESP.class).blueValue.getValue(), Slack.getInstance().getModuleManager().getInstance(PointerESP.class).alphaValue.getValue()).getRGB() : ColorUtil.rainbow(-100, 1.0f, 0.47f).getRGB());
                    }
                    RenderUtil.drawTriAngle(0F, 0F, 2.2F, 3F, c);
                    RenderUtil.drawTriAngle(0F, 0F, 1.5F, 3F, c);
                    RenderUtil.drawTriAngle(0F, 0F, 1.0F, 3F, c);
                    RenderUtil.drawTriAngle(0F, 0F, 0.5F, 3F, c);
                    RenderUtil.drawTriAngle(0F, 0F, 2.2F, 3F, c);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public static void renderOne(float lineWidth) {
        checkSetupFBO();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glClearStencil(0xF);
        GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    }

    public static void renderTwo() {
        GL11.glStencilFunc(GL11.GL_NEVER, 0, 0xF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    public static void renderThree() {
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    }

    public static void renderFour(Color color) {
        GL11.glColor4d(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(1.0F, -2000000F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
    }

    public static void renderFive() {
        GL11.glPolygonOffset(1.0F, 2000000F);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPopAttrib();
    }

    public static void checkSetupFBO() {
        // Gets the FBO of Minecraft
        final Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();

        // Check if FBO isn't null
        if(fbo != null) {
            // Checks if screen has been resized or new FBO has been created
            if(fbo.depthBuffer > -1) {
                // Sets up the FBO with depth and stencil extensions (24/8 bit)
                setupFBO(fbo);
                // Reset the ID to prevent multiple FBO's
                fbo.depthBuffer = -1;
            }
        }
    }

    /**
     * Sets up the FBO with depth and stencil
     *
     * @param fbo Framebuffer
     */
    private static void setupFBO(Framebuffer fbo) {
        // Deletes old render buffer extensions such as depth
        // Args: Render Buffer ID
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        // Generates a new render buffer ID for the depth and stencil extension
        final int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
        // Binds new render buffer by ID
        // Args: Target (GL_RENDERBUFFER_EXT), ID
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
        // Adds the depth and stencil extension
        // Args: Target (GL_RENDERBUFFER_EXT), Extension (GL_DEPTH_STENCIL_EXT),
        // Width, Height
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        // Adds the stencil attachment
        // Args: Target (GL_FRAMEBUFFER_EXT), Attachment
        // (GL_STENCIL_ATTACHMENT_EXT), Target (GL_RENDERBUFFER_EXT), ID
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
        // Adds the depth attachment
        // Args: Target (GL_FRAMEBUFFER_EXT), Attachment
        // (GL_DEPTH_ATTACHMENT_EXT), Target (GL_RENDERBUFFER_EXT), ID
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
    }

    public static void startStencil() {
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Always pass the stencil test
        GL11.glStencilMask(0xFF); // Write to all bits of the stencil buffer
    }

    public static void startRenderInStencil(boolean inverse) {
        if (inverse) {
            GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1
        } else {
            GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF); // Pass test if stencil value is 1
        }
        GL11.glStencilMask(0x00); // Disable writing to the stencil buffer
    }

    public static void endStencil(){
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    public static void drawCircle(float x, float y, float radius, int color) {
        drawRoundedRect(x - radius, y - radius, x + radius, y + radius, radius * 2, color);
    }

    public static void resetCaps() {
        resetCaps("COMMON");
    }
}
