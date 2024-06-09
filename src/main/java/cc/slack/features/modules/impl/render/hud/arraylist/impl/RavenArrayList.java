// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render.hud.arraylist.impl;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.impl.render.HUD;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.utils.client.mc;
import cc.slack.utils.render.ColorUtil;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.minecraft.client.gui.Gui.drawRect;

public class RavenArrayList implements IArraylist {

    private class Pair {
        String first;
        String second;
    }

    private final List<Pair> modules = new ArrayList<>();

    @Override
    public void onUpdate(UpdateEvent event) {
        modules.clear();
        for (Module module : Slack.getInstance().getModuleManager().getModules()) {
            if (module.isToggle() || !module.disabledTime.hasReached(300)) {
                String displayName = module.getDisplayName();
                String mode = module.getMode();
                String key =  Keyboard.getKeyName(module.getKey());
                if (mode != null && !mode.isEmpty()) {
                    displayName += "§7 - " + mode;
                }
                if (!key.contains("NONE")) {
                    displayName += "§7 [" + Keyboard.getKeyName(module.getKey()) + "]";
                }

                Pair pair = new Pair();
                pair.first = displayName;
                pair.second = module.getName();

                modules.add(pair);
            }
        }

        modules.sort((a, b) -> Integer.compare(mc.getFontRenderer().getStringWidth(b.first), mc.getFontRenderer().getStringWidth(a.first)));
    }

    @Override
    public void onRender(RenderEvent event) {
        int y = 3;
        double c = 0;
        for (Pair module : modules) {
            int stringLength = mc.getFontRenderer().getStringWidth(module.first);
            Module m = Slack.getInstance().getModuleManager().getModuleByName(module.second);
            double ease;

            if (m.isToggle()) {
                if (m.enabledTime.hasReached(250)) {
                    ease = 0;
                } else {
                    ease = Math.pow(1 - (m.enabledTime.elapsed() / 250.0), 1);
                }
            } else {
                ease = Math.pow(m.disabledTime.elapsed() / 250.0, 1);
            }

            ease = 1 - 1.2 * ease;

            mc.getFontRenderer().drawStringWithShadow(module.first, event.getWidth() - stringLength * ease - 3, y,  ColorUtil.getColor(Slack.getInstance().getModuleManager().getInstance(HUD.class).theme.getValue(), c).getRGB());
            y += (int) ((mc.getFontRenderer().FONT_HEIGHT + 3) * Math.pow((ease + 0.2) / 1.2, 0.0));
            c += 0.15;
        }
    }

    @Override
    public String toString() {
        return "Raven";
    }
}
