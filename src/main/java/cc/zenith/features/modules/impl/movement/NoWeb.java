package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.player.MovementUtil;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "NoWeb",
        category = Category.MOVEMENT
)
public class NoWeb extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Vanilla", "FastFall", "Verus"});

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
            case "fastfall":
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
