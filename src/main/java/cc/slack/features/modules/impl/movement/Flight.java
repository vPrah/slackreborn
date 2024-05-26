// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.CollideEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.MoveEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.impl.movement.flights.IFlight;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.impl.movement.flights.impl.others.AirJumpFlight;
import cc.slack.features.modules.impl.movement.flights.impl.others.ChunkFlight;
import cc.slack.features.modules.impl.movement.flights.impl.others.CollideFlight;
import cc.slack.features.modules.impl.movement.flights.impl.vanilla.VanillaFlight;
import cc.slack.features.modules.impl.movement.flights.impl.verus.VerusFlight;
import cc.slack.features.modules.impl.movement.flights.impl.verus.VerusJumpFlight;
import io.github.nevalackin.radbus.Listen;
import org.lwjgl.input.Keyboard;


@ModuleInfo(
        name = "Flight",
        category = Category.MOVEMENT,
        key = Keyboard.KEY_F
)
public class Flight extends Module {

    private final ModeValue<IFlight> mode = new ModeValue<>(new IFlight[]{

            // Vanilla
            new VanillaFlight(),

            // Verus
            new VerusJumpFlight(),
            new VerusFlight(),

            // Others
            new ChunkFlight(),
            new CollideFlight(),
            new AirJumpFlight()
    });

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
