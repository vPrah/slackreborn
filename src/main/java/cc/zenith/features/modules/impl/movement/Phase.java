package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.features.modules.api.settings.impl.NumberValue;
import cc.zenith.utils.client.MC;
import cc.zenith.utils.network.PacketUtil;
import cc.zenith.utils.player.MoveUtil;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "Phase",
        category = Category.MOVEMENT
)
public class Phase extends Module {

    private final ModeValue<String> mode = new ModeValue<>("Mode", new String[]{"Clip"});
    private final NumberValue<Double> offset = new NumberValue<>("Offset", 1D, 0.1D, 8D, 0.1D);

    boolean insideBlock;

    public Phase() {
        super();
        addSettings(mode, offset);
    }

    @Override
    public void onEnable() {
        insideBlock = false;
    }

    @Listen
    public void onMove(MoveEvent event) {
        switch (mode.getValue().toLowerCase()) {
            case "clip":
            double yaw = Math.toRadians(MC.getPlayer().rotationYaw);
            if (MC.getPlayer().isCollidedHorizontally) {
                insideBlock = true;
                MC.getPlayer().setPosition(
                        MC.getPlayer().posX + -Math.sin(yaw) * 0.005,
                        MC.getPlayer().posY,
                        MC.getPlayer().posZ + Math.cos(yaw) * 0.005);
            } else if (insideBlock) {
                MC.getPlayer().setPosition(
                        MC.getPlayer().posX + -Math.sin(yaw) * offset.getValue(),
                        MC.getPlayer().posY,
                        MC.getPlayer().posZ + Math.cos(yaw) * offset.getValue());
                insideBlock = false;
            }
            break;
        }
    }

}
