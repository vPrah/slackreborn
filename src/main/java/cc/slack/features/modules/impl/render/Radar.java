package cc.slack.features.modules.impl.render;

import cc.slack.Slack;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.other.MathUtil;
import cc.slack.utils.render.ColorUtil;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.Iterator;

@ModuleInfo(
        name = "Radar",
        category = Category.RENDER
)
public class Radar extends Module {

    private final NumberValue<Float> xValue = new NumberValue<>("Pos X", 8.0F, 1.0F, 300.0F, 1F);
    private final NumberValue<Float> yValue = new NumberValue<>("Pos Y", 178F, 1.0F, 300.0F, 1F);
    private final NumberValue<Float> scaleValue = new NumberValue<>("Scale", 11.5F, 1.0F, 30.0F, 0.1F);
    private final BooleanValue roundedValue = new BooleanValue("Rounded", false);

    public Radar() {
        addSettings(xValue, yValue,scaleValue,roundedValue);
    }

    @Listen
    public void onRender (RenderEvent event) {
        Color ct = ColorUtil.getColor();
        float x = xValue.getValue();
        float y = yValue.getValue();
        float width = scaleValue.getValue() * 10;
        float height = scaleValue.getValue() * 10;
        if (!roundedValue.getValue()) {
            Gui.drawRect(x, y, (x + width), (y + height), (new Color(-1121968096, true)).getRGB());
            Gui.drawRect((x + 1.0F), (double)(y + height) - 1.5, (x + width - 1.0F), (y + height - 1.0F), (ct).getRGB());
        } else {
            RenderUtil.drawRoundedRect(x, y, (x + width), (y + height), 8F, (new Color(-1121968096, true)).getRGB());
            Gui.drawRect((x + 5.0F), (double)(y + height) - 1.5, (x + width - 5.0F), (y + height - 1.0F), (ct).getRGB());
        }
        float centerX = x + width / 2.0F;
        float centerY = y + height / 2.0F;
        Iterator var8 = mc.getWorld().getLoadedEntityList().iterator();

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

}
