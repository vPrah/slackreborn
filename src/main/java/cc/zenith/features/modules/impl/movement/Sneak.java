package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.MotionEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.utils.client.MC;
import cc.zenith.utils.player.MoveUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C0BPacketEntityAction;


@ModuleInfo(
        name = "Sneak",
        category = Category.MOVEMENT
)
public class Sneak extends Module {
    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Legit", "Vanilla"});

    public Sneak() {
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        if (MC.getPlayer() == null)
            return;

        if ("vanilla".equalsIgnoreCase(mode.getValue())) {
            MC.getNetHandler().addToSendQueue(new C0BPacketEntityAction(MC.getPlayer(), C0BPacketEntityAction.Action.START_SNEAKING));
        }
    }

    @Listen
    public void onMotion(MotionEvent event) {
        switch (mode.getValue().toLowerCase()) {
            case "legit":
                MC.getGameSettings().keyBindSneak.pressed = true;
                break;
        }
    }

    @Override
    public void onDisable() {
        if (MC.getPlayer() == null)
            return;
        super.onDisable();
    }
}
