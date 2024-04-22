package cc.slack.features.modules.impl.movement.speeds.hop;

import cc.slack.events.impl.player.MoveEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;

public class HypixelHopSpeed implements ISpeed {



    @Override
    public void onMove(MoveEvent event) {
        if (mc.getPlayer().onGround) {
            if (MovementUtil.isMoving()) {
                mc.getPlayer().jump();
                MovementUtil.minLimitStrafe(0.46f);
            }
        } else {
            mc.getPlayer().motionX *= 1.001;
            mc.getPlayer().motionZ *= 1.001;
        }

    }

    @Override
    public String toString() {
        return "Hypixel Hop";
    }
}
