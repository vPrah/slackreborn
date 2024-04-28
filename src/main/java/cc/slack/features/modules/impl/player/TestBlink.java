// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.player;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.BlinkUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@ModuleInfo(
        name = "TestBlink",
        category = Category.PLAYER
)
public class TestBlink extends Module {


    List<GameSettings> inputReplay = new ArrayList<>();
    private Minecraft startingMC = mc.getMinecraft();
    private int ticks = 0;

    @Override
    public void onEnable() {
        BlinkUtil.enable(true, true);
        inputReplay.clear();
        startingMC = mc.getMinecraft();
        ticks = 0;
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        inputReplay.add(mc.getGameSettings());
        ticks++;
    }

    @Override
    public void onDisable() {
        BlinkUtil.clearPackets(false, true);
        BlinkUtil.setConfig(true, false);
        Minecraft.setMinecraft(startingMC);
        for (int i = 1; i <= ticks; i++) {
            try {
                mc.getMinecraft().gameSettings = inputReplay.get(i);
                mc.getMinecraft().runTick();
            } catch (IOException ignored) {
                // womp womp
            }
        }
        BlinkUtil.disable(true);
    }


}
