// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render.hud.arraylist.impl;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.utils.client.mc;
import cc.slack.utils.font.Fonts;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Basic2ArrayList implements IArraylist {

    List<String> modules = new ArrayList<>();

    @Override
    public void onUpdate(UpdateEvent event) {
        modules.clear();
        for (Module module : Slack.getInstance().getModuleManager().getModules()) {
            if (module.isToggle()) modules.add(module.getDisplayName());
        }
        Collections.sort(modules);
    }

    @Override
    public void onRender(RenderEvent event) {
        int y = 1;
        for (String module : modules) {
            Gui.drawRect(((int) event.getWidth()) - 1, 0, ((int) event.getWidth()), mc.getFontRenderer().FONT_HEIGHT, Color.WHITE.getRGB());
            Fonts.SFBold18.drawString(module, event.getWidth() - Fonts.SFBold18.getStringWidth(module) - 4, y, Color.WHITE.getRGB());
            y += mc.getFontRenderer().FONT_HEIGHT;
        }
    }

    @Override
    public String toString() {
        return "Basic 2";
    }
}
