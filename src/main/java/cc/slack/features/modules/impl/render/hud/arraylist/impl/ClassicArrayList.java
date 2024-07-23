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
import org.lwjgl.input.Keyboard;

public class ClassicArrayList implements IArraylist {

    private class Pair {
        String first;
        String second;
    }


    private final Map<Module, Boolean> moduleStates = new HashMap<>();
    List<Pair> modules = new ArrayList<>();

    @Override
    public void onUpdate(UpdateEvent event) {
        modules.clear();
        for (Module module : Slack.getInstance().getModuleManager().getModules() ) {
            if (!module.render) continue;
            boolean wasEnabled = moduleStates.getOrDefault(module, false);
            boolean isEnabled = module.isToggle();

            moduleStates.put(module, isEnabled);

            if (module.isToggle() || !module.disabledTime.hasReached(300)) {
                String displayName = module.getDisplayName();
                String mode = module.getMode();
                String key =  Keyboard.getKeyName(module.getKey());
                if (mode != null && !mode.isEmpty() && Slack.getInstance().getModuleManager().getInstance(HUD.class).tags.getValue()) {
                    switch (Slack.getInstance().getModuleManager().getInstance(HUD.class).tagsMode.getValue()) {
                        case "(Mode)":
                            displayName += "§7 (" + mode + ")";
                            break;
                        case "[Mode]":
                            displayName += "§7 [" + mode + "]";
                            break;
                        case "<Mode>":
                            displayName += "§7 <" + mode + ">";
                            break;
                        case "| Mode":
                            displayName += "§7 | " + mode;
                            break;
                        case "-> Mode":
                            displayName += "§7 -> " + mode;
                            break;
                        case "- Mode":
                            displayName += "§7 - " + mode;
                            break;
                    }
                }
                if (!key.contains("NONE") && Slack.getInstance().getModuleManager().getInstance(HUD.class).binds.getValue()) {
                    switch (Slack.getInstance().getModuleManager().getInstance(HUD.class).bindsMode.getValue()) {
                        case "(Mode)":
                            displayName += "§7 (" + Keyboard.getKeyName(module.getKey()) + ")";                            break;
                        case "[Mode]":
                            displayName += "§7 [" + Keyboard.getKeyName(module.getKey()) + "]";                            break;
                        case "<Mode>":
                            displayName += "§7 <" + Keyboard.getKeyName(module.getKey()) + ">";
                            break;
                        case "| Mode":
                            displayName += "§7 | " + Keyboard.getKeyName(module.getKey());
                            break;
                        case "-> Mode":
                            displayName += "§7 -> " + Keyboard.getKeyName(module.getKey());
                            break;
                        case "- Mode":
                            displayName += "§7 - " + Keyboard.getKeyName(module.getKey());
                            break;
                    }
                }

                Pair pair = new Pair();
                pair.first = displayName;
                pair.second = module.getName();

                modules.add(pair);
            }
        }
        switch (Slack.getInstance().getModuleManager().getInstance(HUD.class).arraylistFont.getValue()) {
            case "Apple":
                modules.sort((a, b) -> Integer.compare(Fonts.apple18.getStringWidth(b.first), Fonts.apple18.getStringWidth(a.first)));
                break;
            case "Poppins":
                modules.sort((a, b) -> Integer.compare(Fonts.poppins18.getStringWidth(b.first), Fonts.poppins18.getStringWidth(a.first)));
                break;
            case "Roboto":
                modules.sort((a, b) -> Integer.compare(Fonts.roboto18.getStringWidth(b.first), Fonts.apple18.getStringWidth(a.first)));
                break;
        }
    }


    @Override
    public void onRender(RenderEvent event) {
        int y = 3;
        double c = 0;
        int stringLength = 0;

        for (Pair module : modules) {
            switch (Slack.getInstance().getModuleManager().getInstance(HUD.class).arraylistFont.getValue()) {
                case "Apple":
                    stringLength = Fonts.apple18.getStringWidth(module.first);
                    break;
                case "Poppins":
                    stringLength = Fonts.poppins18.getStringWidth(module.first);
                    break;
                case "Roboto":
                    stringLength = Fonts.roboto18.getStringWidth(module.first);
                    break;
            }
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

            switch (Slack.getInstance().getModuleManager().getInstance(HUD.class).arraylistFont.getValue()) {
                case "Apple":
                    if (Slack.getInstance().getModuleManager().getInstance(HUD.class).arraylistBackground.getValue()) {
                        drawRoundedRect((int) (event.getWidth() - stringLength * ease - 5), y - 2, (int) (event.getWidth() - stringLength * ease + stringLength + 3) - (int) (event.getWidth() - stringLength * ease - 5), y + Fonts.poppins18.getHeight() + 1 - y - 1, 1.0f, 0x80000000);
                    }
                    Fonts.apple18.drawStringWithShadow(module.first, event.getWidth() - stringLength * ease - 3, y, ColorUtil.getColor(Slack.getInstance().getModuleManager().getInstance(HUD.class).theme.getValue(), c).getRGB());
                    y += (int) ((Fonts.apple18.getHeight() + 3) * Math.max(0, (ease + 0.2)/1.2));
                    c += 0.13;
                    break;
                case "Poppins":
                    if (Slack.getInstance().getModuleManager().getInstance(HUD.class).arraylistBackground.getValue()) {
                        drawRoundedRect((int) (event.getWidth() - stringLength * ease - 5), y - 2, (int) (event.getWidth() - stringLength * ease + stringLength + 3) - (int) (event.getWidth() - stringLength * ease - 5), y + Fonts.poppins18.getHeight() + 1 - y + 2, 1.0f, 0x80000000);
                    }
                    Fonts.poppins18.drawStringWithShadow(module.first, event.getWidth() - stringLength * ease - 3, y,  ColorUtil.getColor(Slack.getInstance().getModuleManager().getInstance(HUD.class).theme.getValue(), c).getRGB());
                    y += (int) ((Fonts.poppins18.getHeight() + 3) * Math.max(0, (ease + 0.2)/1.2));
                    c += 0.15;
                    break;
                case "Roboto":
                    if (Slack.getInstance().getModuleManager().getInstance(HUD.class).arraylistBackground.getValue()) {
                    drawRoundedRect( (int) (event.getWidth() - stringLength * ease - 5), y - 2, (int) (event.getWidth() - stringLength * ease + stringLength + 3) - (int) (event.getWidth() - stringLength * ease - 5), y + Fonts.poppins18.getHeight() + 1 - y - 1, 1.0f, 0x80000000);
                    }
                    Fonts.roboto18.drawStringWithShadow(module.first, event.getWidth() - stringLength * ease - 3, y, ColorUtil.getColor(Slack.getInstance().getModuleManager().getInstance(HUD.class).theme.getValue(), c).getRGB());
                    y += (int) ((Fonts.roboto18.getHeight() + 3) * Math.max(0, (ease + 0.2)/1.2));
                    c += 0.13;
                    break;
            }
        }
    }



    @Override
    public String toString() {
        return "Classic";
    }

    private void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        RenderUtil.drawRoundedRect(x, y, x + width, y + height, radius, color);
    }
}