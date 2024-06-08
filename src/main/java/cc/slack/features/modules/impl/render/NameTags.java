package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.LivingLabelEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.MathUtil;
import cc.slack.utils.player.AttackUtil;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector4d;
import java.awt.*;


@ModuleInfo(
        name = "NameTags",
        category = Category.RENDER
)
public class NameTags extends Module {

    @Listen
    public void onRender (RenderEvent e) {
        if (e.getState() == RenderEvent.State.RENDER_2D) {
            mc.getWorld().loadedEntityList.forEach(entity -> {
                if (entity instanceof EntityPlayer) {
                    EntityPlayer ent = (EntityPlayer) entity;
                    if (AttackUtil.isTarget(entity) && RenderUtil.isInViewFrustrum(ent.getEntityBoundingBox())) {
                        Vector4d position = RenderUtil.getProjectedEntity(ent, e.getPartialTicks());
                        mc.getEntityRenderer().setupOverlayRendering();
                        if (position != null) {
                            GL11.glPushMatrix();
                            float size = .5f;
                            RenderUtil.drawArmor(ent, (int) (position.x + ((position.z - position.x) / 2)), (int) position.y - 4 - mc.getFontRenderer().FONT_HEIGHT * 2, size);
                            GlStateManager.scale(size, size, size);
                            float x = (float) position.x / size;
                            float x2 = (float) position.z / size;
                            float y = (float) position.y / size;
                            final String nametext = entity.getDisplayName().getFormattedText() + " §7(§f" + MathUtil.roundToPlace(((EntityPlayer) entity).getHealth(), 1) + " §c❤§7)";
                            RenderUtil.drawRoundedRect((x + (x2 - x) / 2) - (mc.getFontRenderer().getStringWidth(nametext) >> 1) - 2, y - mc.getFontRenderer().FONT_HEIGHT - 4, (x + (x2 - x) / 2) + (mc.getFontRenderer().getStringWidth(nametext) >> 1) + 2, y + 2, 3F, new Color(0, 0, 0, 120).getRGB());

                            mc.getFontRenderer().drawStringWithShadow(nametext, (x + ((x2 - x) / 2)) - (mc.getFontRenderer().getStringWidth(nametext) / 2F), y - mc.getFontRenderer().FONT_HEIGHT - 2, getNameColor(ent));
                            GL11.glPopMatrix();
                        }
                    }
                }
            });
        }

    }

    @Listen
    public void onLivingLabel (LivingLabelEvent event) {
        if(event.getEntity() instanceof EntityPlayer && AttackUtil.isTarget(event.getEntity())) {
            event.cancel();
        }
    }


    private int getNameColor(EntityLivingBase ent) {
        if (ent.getDisplayName().equals(mc.getPlayer().getDisplayName())) return new Color(0xFF99ff99).getRGB();
        return new Color(-1).getRGB();
    }

}
