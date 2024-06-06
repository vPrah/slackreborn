// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render.hud.arraylist.impl;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.utils.font.Fonts;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.client.gui.Gui.drawRect;

public class BasicArrayList implements IArraylist {

    private class Pair {
        String first;
        Double second;
    }


    private final Map<Module, Boolean> moduleStates = new HashMap<>();
    List<Pair> modules = new ArrayList<>();

    @Override
    public void onUpdate(UpdateEvent event) {
        modules.clear();
        for (Module module : Slack.getInstance().getModuleManager().getModules()) {
            boolean wasEnabled = moduleStates.getOrDefault(module, false);
            boolean isEnabled = module.isToggle();

            if (isEnabled && !wasEnabled) {
                PlaySound();
            } else if (!isEnabled && wasEnabled) {
                PlaySound();
            }
            moduleStates.put(module, isEnabled);

            if (module.isToggle() || !module.disabledTime.hasReached(300)) {
                String displayName = module.getDisplayName();
                String mode = module.getMode();
                if (mode != null && !mode.isEmpty()) {
                    displayName += " §7" + mode;
                }

                double ease;

                if (module.isToggle()) {
                    if (module.enabledTime.hasReached(300)) {

                        ease = 0;
                    } else {

                        ease = Math.pow(1 - (module.enabledTime.elapsed() / 300.0), 4);
                    }
                } else {

                    ease = Math.pow(module.disabledTime.elapsed() / 300.0, 4);
                }

                Pair pair = new Pair();
                pair.first = displayName;
                pair.second = 1 - 1.2 * ease;

                modules.add(pair);
            }
        }
        modules.sort((a, b) -> Integer.compare(Fonts.apple18.getStringWidth(b.first), Fonts.apple18.getStringWidth(a.first)));
    }


    @Override
    public void onRender(RenderEvent event) {
        int y = 3;

        for (Pair module : modules) {
            int stringLength = Fonts.apple18.getStringWidth(module.first);
            drawRect(
                    (int) (event.getWidth() - stringLength * module.second - 5),
                    y - 2,
                    (int) (event.getWidth() - stringLength * module.second + stringLength + 3),
                    y + Fonts.apple18.getHeight() + 1,
                    0x80000000
            );
            Fonts.apple18.drawStringWithShadow(module.first, event.getWidth() - stringLength * module.second - 3, y, 0x5499C7);
            y += (int) ((Fonts.apple18.getHeight() + 3) * Math.pow((module.second + 0.2)/1.2, 0.5));
        }
    }

    @Override
    public String toString() {
        return "Basic";
    }
    private void PlaySound() {
        ResourceLocation soundLocation = new ResourceLocation("random.orb");
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(soundLocation, 5.0F));
    }

}
