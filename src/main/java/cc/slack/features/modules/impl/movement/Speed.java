package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MoveEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.features.modules.impl.movement.speeds.hop.*;
import cc.slack.features.modules.impl.movement.speeds.vanilla.*;
import cc.slack.features.modules.impl.movement.speeds.lowhops.*;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "Speed",
        category = Category.MOVEMENT,
        key = Keyboard.KEY_B
)
public class Speed extends Module {

    private final ModeValue<ISpeed> mode = new ModeValue<>(new ISpeed[]{

            // Vanilla
            new VanillaSpeed(),
            new StrafeSpeed(),

            // Verus
            new VerusHopSpeed(),

            // Hypixel
            new HypixelHopSpeed(),

            // NCP
            new NCPHopSpeed(),

            // Vulcan
            new VulcanLowSpeed(),
            new VulcanHopSpeed()
    });

    public final NumberValue<Float> vanillaspeed = new NumberValue<>("Vanilla-Speed", 1.0F, 3.0F, 12.0F, 0.10F);

    private final BooleanValue jumpFix = new BooleanValue("JumpFix", true);

    public Speed() {
        super();
        addSettings(mode, vanillaspeed);
    }

    @Override
    public void onEnable() {
        mode.getValue().onEnable();
    }

    @Override
    public void onDisable() {
        if (jumpFix.getValue()) { mc.getGameSettings().keyBindJump.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindJump); }
        mode.getValue().onDisable();
    }

    @Listen
    public void onMove(MoveEvent event) {
        mode.getValue().onMove(event);
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (jumpFix.getValue()) { mc.getGameSettings().keyBindJump.pressed = false; }
        mode.getValue().onUpdate(event);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        mode.getValue().onPacket(event);
    }
}
