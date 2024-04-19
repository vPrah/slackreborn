package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.settings.impl.BooleanValue;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.NumberValue;
import cc.zenith.features.modules.impl.movement.speeds.ISpeed;
import cc.zenith.features.modules.impl.movement.speeds.hops.VerusHopSpeed;
import io.github.nevalackin.radbus.Listen;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "Speed",
        category = Category.MOVEMENT,
        key = Keyboard.KEY_B
)
public class Speed extends Module {

    private final ModeValue<ISpeed> mode = new ModeValue<>(new ISpeed[]{new VerusHopSpeed()});

    public final NumberValue<Double> vspeed = new NumberValue<>("Speed", 0.8D, 0.0D, 2.0D, 1D);

    public Speed() {
        super();
        addSettings(mode, vspeed);
    }

    @Override
    public void onEnable() {
        mode.getValue().onEnable();
    }

    @Override
    public void onDisable() {
        mode.getValue().onDisable();
    }

    @Listen
    public void onMove(MoveEvent event) {
        mode.getValue().onMove(event);
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        mode.getValue().onUpdate(event);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        mode.getValue().onPacket(event);
    }
}
