// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render.hud.arraylist.impl;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.utils.client.mc;
import cc.slack.utils.font.Fonts;
import cc.slack.utils.render.ComparatorStrings;
import cc.slack.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static net.minecraft.client.gui.Gui.drawRect;

public class Basic2ArrayList implements IArraylist {

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
                if (mode != null && !mode.isEmpty()) {
                    displayName += " §7" + mode;
                }

                Pair pair = new Pair();
                pair.first = displayName;
                pair.second = module.getName();

                modules.add(pair);
            }
        }
        modules.sort((a, b) -> Integer.compare(Fonts.poppins18.getStringWidth(b.first), Fonts.poppins18.getStringWidth(a.first)));
    }

    @Override
    public void onRender(RenderEvent event) {
        if (mc.getGameSettings().showDebugInfo) {
            return;
        }
        renderArrayList(event);
    }

    private void renderArrayList(RenderEvent event) {
        int y = 2;

        for (Pair module : modules) {
            int stringLength = Fonts.poppins18.getStringWidth(module.first);
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
            /*
            drawRect(
                    (int) (event.getWidth() - stringLength * ease - 5),
                    y - 2,
                    (int) (event.getWidth() - stringLength * ease + stringLength + 3),
                    y + Fonts.poppins18.getHeight() + 1,
                    0x70000000
            );
             */

            drawRoundedRect( (int) (event.getWidth() - stringLength * ease - 5), y - 2, (int) (event.getWidth() - stringLength * ease + stringLength + 3) - (int) (event.getWidth() - stringLength * ease - 5), y + Fonts.poppins18.getHeight() + 1 - y + 2, 1.0f, 0x80000000);
            Fonts.poppins18.drawStringWithShadow(module.first, event.getWidth() - stringLength * ease - 3, y, 0x5499C7);
            y += (int) ((Fonts.poppins18.getHeight() + 3) * Math.pow((ease + 0.2) / 1.2, 0.0));
        }
    }

    @Override
    public String toString() {
        return "Basic 2";
    }

    private void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        RenderUtil.drawRoundedRect(x, y, x + width, y + height, radius, color);
    }
}
