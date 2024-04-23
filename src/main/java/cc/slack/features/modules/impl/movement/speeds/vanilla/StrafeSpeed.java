package cc.slack.features.modules.impl.movement.speeds.vanilla;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;

public class StrafeSpeed implements ISpeed {

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().onGround && MovementUtil.isMoving()) {
            mc.getPlayer().jump();
        }
        MovementUtil.strafe();
    }

    @Override
    public String toString() {
        return "Strafe";
    }
}
