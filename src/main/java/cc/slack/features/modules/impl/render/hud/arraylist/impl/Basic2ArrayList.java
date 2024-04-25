// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render.hud.arraylist.impl;

import cc.slack.Slack;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.utils.client.mc;
import cc.slack.utils.font.Fonts;
import net.minecraft.client.gui.Gui;

import java.awt.*;


public class Basic2ArrayList implements IArraylist {
    @Override
    public void onRender(RenderEvent event) {
        int y = 1;
        for (Module module : Slack.getInstance().getModuleManager().getModules()) {
            if (!module.isToggle()) continue;
            Gui.drawRect(((int) event.getWidth()) - 1, 0, ((int) event.getWidth()), mc.getFontRenderer().FONT_HEIGHT, Color.WHITE.getRGB());
            Fonts.axi18.drawString(module.getDisplayName(), event.getWidth() - mc.getFontRenderer().getStringWidth(module.getDisplayName()) - 4, y, Color.WHITE.getRGB());
            y += mc.getFontRenderer().FONT_HEIGHT;
        }
    }

    @Override
    public String toString() {
        return "Basic 2";
    }
}
