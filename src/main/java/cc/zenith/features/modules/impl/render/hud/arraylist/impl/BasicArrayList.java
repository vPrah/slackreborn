package cc.zenith.features.modules.impl.render.hud.arraylist.impl;

import cc.zenith.Zenith;
import cc.zenith.events.impl.render.RenderEvent;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.impl.render.HUD;
import cc.zenith.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.zenith.utils.client.ClientInfo;
import cc.zenith.utils.client.MC;
import cc.zenith.utils.font.Fonts;

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
        for (Module module : Zenith.getInstance().getModuleManager().getModules()) {
            if (!module.isToggle()) continue;
            Fonts.apple18.drawStringWithShadow(module.getDisplayName(), event.getWidth() - Fonts.apple18.getStringWidth(module.getDisplayName()), y, Color.WHITE.getRGB());
            y += MC.getFontRenderer().FONT_HEIGHT;
        }
    }

    @Override
    public String toString() {
        return "Basic";
    }
}
