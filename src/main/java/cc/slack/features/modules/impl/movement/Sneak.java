// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.C0BPacketEntityAction;


@ModuleInfo(
        name = "Sneak",
        category = Category.MOVEMENT
)
public class Sneak extends Module {
    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Legit", "Vanilla"});

    public Sneak() {
        super();
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        if (mc.getPlayer() == null)
            return;

        if ("vanilla".equalsIgnoreCase(mode.getValue())) {
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.getPlayer(), C0BPacketEntityAction.Action.START_SNEAKING));
        }
    }

    @Listen
    public void onMotion(MotionEvent event) {
        switch (mode.getValue().toLowerCase()) {
            case "legit":
                mc.getGameSettings().keyBindSneak.pressed = true;
                break;
        }
    }

    @Override
    public void onDisable() {
        if (mc.getPlayer() == null)
            return;
        switch (mode.getValue().toLowerCase()) {
            case "legit":
                mc.getGameSettings().keyBindSneak.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindSneak);
                break;
            case "vanilla":
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.getPlayer(), C0BPacketEntityAction.Action.STOP_SNEAKING));
                break;
        }
        super.onDisable();
    }
}
