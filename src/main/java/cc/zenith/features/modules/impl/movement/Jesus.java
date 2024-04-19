package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.player.MovementUtil;
import io.github.nevalackin.radbus.Listen;
import org.lwjgl.input.Keyboard;


@ModuleInfo(
        name = "Jesus",
        category = Category.MOVEMENT,
        key = Keyboard.KEY_J
)

public class Jesus extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Vanilla", "Verus"});

    public Jesus() {
        super();
        addSettings(mode);
    }

    @Listen
    public void onMove(MoveEvent event) {
        switch (mode.getValue().toLowerCase()) {
            case "vanilla":
                if (mc.getPlayer().isInWater()) {
                    MovementUtil.setSpeed(event, 0.4f);
                    event.setY(0.01);
                }
                break;
            case "verus":
                if (mc.getPlayer().isInWater()) {
                    MovementUtil.setSpeed(event, 0.40f);
                    event.setY(0.405f);
                }
                break;
        }
    }
}
