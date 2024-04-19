package cc.slack.features.modules.impl.render.hud.arraylist.impl;

import cc.slack.Slack;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.impl.render.HUD;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.utils.client.mc;
import cc.slack.utils.font.Fonts;

import java.awt.*;

/**
 * @author skelton
 * @see HUD
 * @since 30/4/2023 15:59
 */
public class BasicArrayList implements IArraylist {
    @Override
    public void onRender(RenderEvent event) {
        int y = 1;
        for (Module module : Slack.getInstance().getModuleManager().getModules()) {
            if (!module.isToggle()) continue;
            Fonts.apple18.drawStringWithShadow(module.getDisplayName(), event.getWidth() - Fonts.apple18.getStringWidth(module.getDisplayName()), y, Color.WHITE.getRGB());
            y += mc.getFontRenderer().FONT_HEIGHT;
        }
    }

    @Override
    public String toString() {
        return "Basic";
    }
}
