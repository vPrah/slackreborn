package cc.zenith.features.modules.impl.combat;

import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.features.modules.api.settings.impl.NumberValue;
import cc.zenith.utils.client.MC;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(
        name = "Velocity",
        category = Category.COMBAT
)

public class Velocity extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Vanilla", "Dev", "Tick"});

    private final NumberValue<Integer> vertical = new NumberValue<>("Vertical", 100, 0, 100, 1);
    private final NumberValue<Integer> horizontal = new NumberValue<>("Horizontal", 0, 0, 100, 1);

    public Velocity() {
        super();
        addSettings(mode, vertical, horizontal);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (MC.getPlayer() == null || MC.getWorld() == null) return;
        if (event.getDirection() != PacketDirection.OUTGOING) return;

        if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) event.getPacket()).getEntityID() == MC.getPlayer().getEntityId()) {
            if (vertical.getValue() != 0 || horizontal.getValue() != 0) {
                S12PacketEntityVelocity s12 = event.getPacket();
                switch (mode.getValue().toLowerCase()) {
                    case "vanilla":
                        s12.setMotionX(s12.getMotionX() * (horizontal.getValue() / 100));
                        s12.setMotionY(s12.getMotionY() * (vertical.getValue() / 100));
                        s12.setMotionZ(s12.getMotionZ() * (horizontal.getValue() / 100));
                        break;
                    case "dev":
                        break;
                    case "tick":
                        break;
                }
            } else {
                event.cancel();
            }
        }
    }

}