package cc.zenith.features.modules.impl.movement.speeds.vanilla;

import cc.zenith.Zenith;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.settings.impl.NumberValue;
import cc.zenith.features.modules.impl.movement.Speed;
import cc.zenith.features.modules.impl.movement.speeds.ISpeed;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.player.MovementUtil;

public class VanillaSpeed implements ISpeed {

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().onGround && MovementUtil.isMoving()) {
            mc.getPlayer().jump();
            MovementUtil.strafe(Zenith.getInstance().getModuleManager().getInstance(Speed.class).vanillaspeed.getValue());
        }
        MovementUtil.strafe();
    }

    @Override
    public String toString() {
        return "Vanilla Hop";
    }
}
