// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render.hud.arraylist.impl;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.utils.font.Fonts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BasicArrayList implements IArraylist {

    List<String> modules = new ArrayList<>();

    @Override
    public void onUpdate(UpdateEvent event) {
        modules.clear();
        for (Module module : Slack.getInstance().getModuleManager().getModules()) {
            if (module.isToggle()) modules.add(module.getDisplayName());
        }
        modules.sort(Comparator.comparingInt(Fonts.apple18::getStringWidth));
    }

    @Override
    public void onRender(RenderEvent event) {
        int y = 3;
        for (String module : modules) {
            Fonts.apple18.drawStringWithShadow(module, event.getWidth() - Fonts.apple18.getStringWidth(module) - 3, y, 0x5499C7);
            y += Fonts.apple18.getHeight() + 2;
        }
    }

    @Override
    public String toString() {
        return "Basic";
    }
}
