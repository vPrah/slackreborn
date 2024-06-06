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
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static net.minecraft.client.gui.Gui.drawRect;


public class Basic2ArrayList implements IArraylist {

    List<String> modules = new ArrayList<>();

    @Override
    public void onUpdate(UpdateEvent event) {
        modules.clear();
        for (Module module : Slack.getInstance().getModuleManager().getModules()) {
            if (module.isToggle()) modules.add(module.getDisplayName());
        }
        modules.sort(Comparator.comparingInt(Fonts.apple18::getStringWidth));
        Collections.reverse(modules);
    }

    @Override
    public void onRender(RenderEvent event) {
        if(mc.getGameSettings().showDebugInfo) {
            return;
        }
        renderArrayList();
        /*
        int y = 1;

        for (String module : modules) {
            drawRect(
                    (int) (event.getWidth() - Fonts.poppins18.getStringWidth(module) - 5),
                    y  - 1,
                    (int) (event.getWidth() - Fonts.poppins18.getStringWidth(module)+ Fonts.poppins18.getStringWidth(module) + 1),
                    y + Fonts.poppins18.getHeight() - 1,
                    0x70000000
            );
            Fonts.poppins18.drawStringWithShadow(module, event.getWidth() - Fonts.poppins18.getStringWidth(module) - 3, y, 0x5499C7);
            y += Fonts.poppins18.getHeight();
        }

         */
    }

    private void renderArrayList() {
        modules.clear();
        int count = 2;
        for (Module module : Slack.getInstance().getModuleManager().getModules()) {
            if (!module.isToggle())
                continue;

            String displayName = module.getName();
            String mode = module.getMode();
            if (mode != null && !mode.isEmpty()) {
                displayName += " §7" + mode;
            }
            modules.add(displayName);
        }

        modules.sort((a, b) -> Integer.compare(Fonts.poppins18.getStringWidth(b), Fonts.poppins18.getStringWidth(a)));

        Collections.sort(modules, new ComparatorStrings());
        for (String m : modules) {
            int x = ( - 2) - (Fonts.poppins18.getStringWidth(m));
            int y = count;
            int x1 = (+ 1);
            int y1 = count + 5;
            drawRect(x - 2 , y - 1 , x1, y1 + 4 , 0x70000000);
            Fonts.poppins18.drawString(m, x, y, 0x5499C7);
            count += 10;
        }
    }

    @Override
    public String toString() {
        return "Basic 2";
    }
}
