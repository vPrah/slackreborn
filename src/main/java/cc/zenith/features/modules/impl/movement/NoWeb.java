package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.utils.client.mc;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "NoWeb",
        category = Category.MOVEMENT
)
public class NoWeb extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Vanilla", "Verus"});

    public NoWeb() {
        super();
        addSettings(mode);
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (!mc.getPlayer().isInWeb) {
            return;
        }

        switch (mode.getValue().toLowerCase()) {
            case "vanilla":
                mc.getPlayer().isInWeb = false;
                break;
            case "verus":
                mc.getPlayer().jumpMovementFactor = (mc.getPlayer().movementInput.moveStrafe != 0f) ? 1.0f : 1.21f;
                if (!mc.getGameSettings().keyBindSneak.isKeyDown()) {
                    mc.getPlayer().motionY = 0.0;
                }
                if (mc.getPlayer().onGround) {
                    mc.getPlayer().jump();
                }
                break;
        }
    }
}
