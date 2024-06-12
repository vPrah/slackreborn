// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.CollideEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.MoveEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.impl.movement.flights.IFlight;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.impl.movement.flights.impl.others.AirJumpFlight;
import cc.slack.features.modules.impl.movement.flights.impl.others.ChunkFlight;
import cc.slack.features.modules.impl.movement.flights.impl.others.CollideFlight;
import cc.slack.features.modules.impl.movement.flights.impl.vanilla.FireballFlight;
import cc.slack.features.modules.impl.movement.flights.impl.vanilla.VanillaFlight;
import cc.slack.features.modules.impl.movement.flights.impl.verus.VerusDamageFlight;
import cc.slack.features.modules.impl.movement.flights.impl.verus.VerusJumpFlight;
import cc.slack.features.modules.impl.movement.flights.impl.verus.VerusPortFlight;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import org.lwjgl.input.Keyboard;


@ModuleInfo(
        name = "Flight",
        category = Category.MOVEMENT,
        key = Keyboard.KEY_F
)
public class Flight extends Module {

    public final ModeValue<IFlight> mode = new ModeValue<>(new IFlight[]{

            // Vanilla
            new VanillaFlight(),
            new FireballFlight(),

            // Verus
            new VerusJumpFlight(),
            new VerusDamageFlight(),
            new VerusPortFlight(),

            // Others
            new ChunkFlight(),
            new CollideFlight(),
            new AirJumpFlight()
    });

    public final BooleanValue vulcanClip = new BooleanValue("Vulcan CanClip", true);

    public Flight() {
        super();
        addSettings(mode, vulcanClip);
    }

    @Override
    public void onEnable() {
        mode.getValue().onEnable();
    }

    @Override
    public void onDisable() {
        mc.getTimer().timerSpeed = 1F;
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

    @Override
    public String getMode() { return mode.getValue().toString(); }

}
