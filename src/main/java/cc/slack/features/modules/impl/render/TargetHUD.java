// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;

import java.awt.*;

@ModuleInfo(
        name = "TargetHUD",
        category = Category.RENDER
)
public class TargetHUD extends Module {

    private final NumberValue<Integer> posX = new NumberValue<>("Pos-X", 100, -100, 500, 10);
    private final NumberValue<Integer> posY = new NumberValue<>("Pos-Y", 10, -100, 500, 10);


    public TargetHUD() {
        addSettings(posX, posY);
    }

    private EntityPlayer player;
    private int ticksSinceAttack;

    @Override
    public void onEnable() {
        player = null;
    }

    @Listen
    @SuppressWarnings("unused")
    public void onUpdate(UpdateEvent event) {
        ticksSinceAttack++;

        if (ticksSinceAttack > 20) {
            player = null;
        }

        if (mc.getCurrentScreen() instanceof GuiChat) {
            player = mc.getPlayer();
            ticksSinceAttack = 0;
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity wrapper = event.getPacket();
            if (wrapper.getEntityFromWorld(mc.getWorld()) instanceof EntityPlayer && wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
                ticksSinceAttack = 0;
                player = (EntityPlayer) wrapper.getEntityFromWorld(mc.getWorld());
            }
        }
    }

    @Listen
    public void onRender(RenderEvent event) {
        if(event.getState() != RenderEvent.State.RENDER_2D) return;

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int x = (sr.getScaledWidth() / 2) + posX.getValue(), y = (sr.getScaledHeight() / 2) + posY.getValue();
        if (player == null)
            return;
        drawRect(x, y, 120, 40, new Color(0, 0, 0, 120).getRGB());
        mc.getFontRenderer().drawString(player.getCommandSenderName(), x + 45, y + 8, 0x5499C7);
        double offset = -(player.hurtTime * 20);
        Color color = new Color(255, (int) (255 + offset), (int) (255 + offset));
        GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
        mc.getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(x + 5, y + 5, 3, 3, 3, 3, 30, 30, 24, 24);
        GlStateManager.color(1, 1, 1, 1);

        drawRect(x + 45, y + 20, 70, 15, new Color(255, 255, 255, 120).getRGB());

        drawRect(x + 45, y + 20, (int) (70 * (player.getHealth() / player.getMaxHealth())), 15, new Color(90, 150, 200, 200).getRGB());

        String s = (int) ((player.getHealth() / player.getMaxHealth()) * 100) + "%";
        mc.getFontRenderer().drawString(s, x + 45 + (70 / 2) - (mc.getFontRenderer().getStringWidth(s) / 2),
                y + 20 + (15 / 2) - (mc.getFontRenderer().FONT_HEIGHT / 2) + 1, -1);
    }

    private void drawRect(int x, int y, int width, int height, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }


}
