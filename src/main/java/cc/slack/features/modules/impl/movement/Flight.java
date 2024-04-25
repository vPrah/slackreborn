// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.CollideEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.MoveEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.impl.movement.flights.IFlight;
import cc.slack.features.modules.impl.movement.flights.impl.*;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import io.github.nevalackin.radbus.Listen;
import org.lwjgl.input.Keyboard;


@ModuleInfo(
        name = "Flight",
        category = Category.MOVEMENT,
        key = Keyboard.KEY_F
)
public class Flight extends Module {

    private final ModeValue<IFlight> mode = new ModeValue<>(new IFlight[]{new VanillaFlight(), new VerusJumpFlight(), new VerusFlight(), new VerusJumpFlight(), new ChunkFlight(), new CollideFlight()});

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
