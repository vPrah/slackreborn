package cc.zenith.features.modules.impl.movement;

import cc.zenith.Zenith;
import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.NumberValue;
import cc.zenith.features.modules.impl.movement.speeds.ISpeed;
import cc.zenith.features.modules.impl.movement.speeds.hop.HypixelHopSpeed;
import cc.zenith.features.modules.impl.movement.speeds.hop.VerusHopSpeed;
import cc.zenith.features.modules.impl.movement.speeds.hop.VulcanHopSpeed;
import cc.zenith.features.modules.impl.movement.speeds.vanilla.VanillaSpeed;
import cc.zenith.features.modules.impl.movement.speeds.yport.VulcanLowSpeed;
import io.github.nevalackin.radbus.Listen;
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

            // Verus
            new VerusHopSpeed(),

            // Hypixel
            new HypixelHopSpeed(),

            // Vulcan
            new VulcanLowSpeed(),
            new VulcanHopSpeed()
    });

    public final NumberValue<Float> vanillaspeed = new NumberValue<>("Vanilla-Speed", 1.0F, 3.0F, 12.0F, 0.10F);


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
