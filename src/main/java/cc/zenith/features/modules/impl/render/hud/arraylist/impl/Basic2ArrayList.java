package cc.zenith.features.modules.impl.render.hud.arraylist.impl;

import cc.zenith.Zenith;
import cc.zenith.events.impl.render.RenderEvent;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.impl.render.HUD;
import cc.zenith.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.zenith.utils.client.MC;
import cc.zenith.utils.font.Fonts;
import net.minecraft.client.gui.Gui;

import java.awt.*;


public class Basic2ArrayList implements IArraylist {
    @Override
    public void onRender(RenderEvent event) {
        int y = 1;
        for (Module module : Zenith.getInstance().getModuleManager().getModules()) {
            if (!module.isToggle()) continue;
            Gui.drawRect(((int) event.getWidth()) - 1, 0, ((int) event.getWidth()), MC.getFontRenderer().FONT_HEIGHT, Color.WHITE.getRGB());
            Fonts.axi18.drawString(module.getDisplayName(), event.getWidth() - MC.getFontRenderer().getStringWidth(module.getDisplayName()) - 4, y, Color.WHITE.getRGB());
            y += MC.getFontRenderer().FONT_HEIGHT;
        }
    }

    @Override
    public String toString() {
        return "Basic 2";
    }
}
