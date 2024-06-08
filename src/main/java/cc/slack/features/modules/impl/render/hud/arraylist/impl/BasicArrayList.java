// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render.hud.arraylist.impl;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.impl.render.HUD;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.utils.font.Fonts;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import cc.slack.utils.render.ColorUtil;
import cc.slack.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import static net.minecraft.client.gui.Gui.drawRect;

public class BasicArrayList implements IArraylist {

    private class Pair {
        String first;
        String second;
    }


    private final Map<Module, Boolean> moduleStates = new HashMap<>();
    List<Pair> modules = new ArrayList<>();

    @Override
    public void onUpdate(UpdateEvent event) {
        modules.clear();
        for (Module module : Slack.getInstance().getModuleManager().getModules()) {
            boolean wasEnabled = moduleStates.getOrDefault(module, false);
            boolean isEnabled = module.isToggle();

            moduleStates.put(module, isEnabled);

            if (module.isToggle() || !module.disabledTime.hasReached(300)) {
                String displayName = module.getDisplayName();
                String mode = module.getMode();
                String key =  Keyboard.getKeyName(module.getKey());
                if (mode != null && !mode.isEmpty()) {
                    displayName += "§7 - " + mode;
                }
                if (!key.contains("NONE") && Slack.getInstance().getModuleManager().getInstance(HUD.class).binds.getValue()) {
                    displayName += "§7 [" + Keyboard.getKeyName(module.getKey()) + "]";
                }

                Pair pair = new Pair();
                pair.first = displayName;
                pair.second = module.getName();

                modules.add(pair);
            }
        }
        modules.sort((a, b) -> Integer.compare(Fonts.apple18.getStringWidth(b.first), Fonts.apple18.getStringWidth(a.first)));
    }


    @Override
    public void onRender(RenderEvent event) {
        int y = 3;
        double c = 0;

        for (Pair module : modules) {
            int stringLength = Fonts.apple18.getStringWidth(module.first);
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
                    y + Fonts.apple18.getHeight() + 1,
                    0x80000000
            );
             */
            drawRoundedRect( (int) (event.getWidth() - stringLength * ease - 5), y - 2, (int) (event.getWidth() - stringLength * ease + stringLength + 3) - (int) (event.getWidth() - stringLength * ease - 5), y + Fonts.poppins18.getHeight() + 1 - y - 1, 1.0f, 0x80000000);
            Fonts.apple18.drawStringWithShadow(module.first, event.getWidth() - stringLength * ease - 3, y, ColorUtil.getColor(Slack.getInstance().getModuleManager().getInstance(HUD.class).theme.getValue(), c).getRGB());
            y += (int) ((Fonts.apple18.getHeight() + 3) * (ease + 0.2)/1.2);
            c += 0.15;
        }
    }



    @Override
    public String toString() {
        return "Basic";
    }

    private void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        RenderUtil.drawRoundedRect(x, y, x + width, y + height, radius, color);
    }
}