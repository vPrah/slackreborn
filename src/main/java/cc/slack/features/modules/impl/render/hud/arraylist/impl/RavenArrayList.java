// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render.hud.arraylist.impl;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.utils.client.mc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static net.minecraft.client.gui.Gui.drawRect;

public class RavenArrayList implements IArraylist {

    List<String> modules = new ArrayList<>();

    @Override
    public void onUpdate(UpdateEvent event) {
        modules.clear();
        for (Module module : Slack.getInstance().getModuleManager().getModules()) {
            if (module.isToggle()) modules.add(module.getDisplayName() + " " + module.getMode());
        }
        modules.sort(Comparator.comparingInt(mc.getFontRenderer()::getStringWidth));
        Collections.reverse(modules);
    }

    @Override
    public void onRender(RenderEvent event) {
        int y = 3;

        for (String module : modules) {
            mc.getFontRenderer().drawStringWithShadow(module, event.getWidth() - mc.getFontRenderer().getStringWidth(module) - 3, y, 0x5499C7);
            y += mc.getFontRenderer().FONT_HEIGHT + 2;
        }
    }

    @Override
    public String toString() {
        return "Raven";
    }
}
