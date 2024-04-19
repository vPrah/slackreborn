package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.CollideEvent;
import cc.zenith.events.impl.player.MotionEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.features.modules.api.settings.impl.MultiSelectValue;
import cc.zenith.features.modules.api.settings.impl.NumberValue;
import cc.zenith.features.modules.impl.movement.flights.IFlight;
import cc.zenith.features.modules.impl.movement.flights.impl.*;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.utils.other.PrintUtil;
import io.github.nevalackin.radbus.Listen;
import org.lwjgl.input.Keyboard;


@ModuleInfo(
        name = "Flight",
        category = Category.MOVEMENT,
        key = Keyboard.KEY_F
)
public class Flight extends Module {

    private final ModeValue<IFlight> mode = new ModeValue<>(new IFlight[]{
            new VanillaFlight(),
            new VerusJumpFlight(), new VerusFlight(),
            new ChunkFlight()
    });

    public final NumberValue<Float> vanillaspeed = new NumberValue<>("Vanilla-Speed", 3.0F, 0.0F, 5.0F, 0.10F);

    public Flight() {
        super();
        addSettings(mode);
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

    @Listen
    public void onCollide(CollideEvent event) {
        mode.getValue().onCollide(event);
    }

    @Listen
    public void onMotion(MotionEvent event) {
        mode.getValue().onMotion(event);
    }

}
