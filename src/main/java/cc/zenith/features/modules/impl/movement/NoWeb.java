package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.utils.client.MC;
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
        if (!MC.getPlayer().isInWeb) {
            return;
        }

        switch (mode.getValue().toLowerCase()) {
            case "vanilla":
                MC.getPlayer().isInWeb = false;
                break;
            case "verus":
                MC.getPlayer().jumpMovementFactor = (MC.getPlayer().movementInput.moveStrafe != 0f) ? 1.0f : 1.21f;
                if (!MC.getGameSettings().keyBindSneak.isKeyDown()) {
                    MC.getPlayer().motionY = 0.0;
                }
                if (MC.getPlayer().onGround) {
                    MC.getPlayer().jump();
                }
                break;
        }
    }
}
