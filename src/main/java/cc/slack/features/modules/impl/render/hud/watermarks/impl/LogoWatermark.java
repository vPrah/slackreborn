package cc.slack.features.modules.impl.render.hud.watermarks.impl;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.impl.render.hud.watermarks.IWatermarks;
import cc.slack.utils.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class LogoWatermark implements IWatermarks {
    @Override
    public void onRender(RenderEvent event) {
        renderLogo();
    }

    @Override
    public void onUpdate(UpdateEvent event) {

    }

    private void renderLogo() {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        RenderUtil.drawImage(new ResourceLocation("slack/menu/hud.png"), 4, 4, 20, 33);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    @Override
    public String toString() {
        return "Logo";
    }
}
