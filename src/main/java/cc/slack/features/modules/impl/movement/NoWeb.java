// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "NoWeb",
        category = Category.MOVEMENT
)
public class NoWeb extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Vanilla", "Fast Fall", "Verus"});

    public NoWeb() {
        super();
        addSettings(mode);
    }

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate(UpdateEvent event) {
        if (!mc.getPlayer().isInWeb) {
            return;
        }

        switch (mode.getValue().toLowerCase()) {
            case "vanilla":
                mc.getPlayer().isInWeb = false;
                break;
            case "fast fall":
                if (mc.getPlayer().onGround) mc.getPlayer().jump();
                if (mc.getPlayer().motionY > 0f) {
                    mc.getPlayer().motionY -= mc.getPlayer().motionY * 2;
                }
                break;
            case "verus":
                MovementUtil.strafe(1.00f);
                if (!mc.getGameSettings().keyBindJump.isKeyDown() && !mc.getGameSettings().keyBindSneak.isKeyDown()) {
                    mc.getPlayer().motionY = 0.00D;
                }
                if (mc.getGameSettings().keyBindJump.isKeyDown()) {
                    mc.getPlayer().motionY = 4.42D;
                }
                if (mc.getGameSettings().keyBindSneak.isKeyDown()) {
                    mc.getPlayer().motionY = -4.42D;
                }
                break;
        }
    }
}
