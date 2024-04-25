package cc.slack.features.modules.impl.player;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.BlinkUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;

import java.io.IOException;


@ModuleInfo(
        name = "TestBlink",
        category = Category.PLAYER
)
public class TestBlink extends Module {


    private Minecraft startingMC = mc.getMinecraft();
    private int ticks = 0;

    @Override
    public void onEnable() {
        BlinkUtil.enable(true, true);
        startingMC = mc.getMinecraft();
        ticks = 0;
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        ticks++;
    }

    @Override
    public void onDisable() {
        BlinkUtil.clearPackets(false, true);
        Minecraft.setMinecraft(startingMC);
        for (int i = 1; i <= ticks; i++) {
            if (i == ticks) {
                BlinkUtil.disable(true);
            }
            try {
                mc.getMinecraft().runTick();
            } catch (IOException ignored) {
                // womp womp
            }
        }
    }


}
