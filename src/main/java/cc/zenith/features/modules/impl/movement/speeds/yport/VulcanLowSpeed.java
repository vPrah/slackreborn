package cc.slack.features.modules.impl.movement.speeds.yport;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;

public class VulcanLowSpeed implements ISpeed {


    int offGroundTicks;

    @Override
    public void onEnable() {
        offGroundTicks = 0;
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        offGroundTicks++;
        mc.getPlayer().jumpMovementFactor = 0.0245f;
        if (mc.getPlayer().onGround && MovementUtil.isMoving()) {
            mc.getPlayer().jump();
            MovementUtil.strafe();
            if (MovementUtil.getSpeed() < 0.5f) {
                MovementUtil.strafe(0.484f);
            }
            offGroundTicks = 0;
        } else if (offGroundTicks == 5) {
            mc.getPlayer().motionY = -0.17D;
        }
        if (MovementUtil.getSpeed() < 0.215F) {
            MovementUtil.strafe(0.215f);
        }
    }

    @Override
    public String toString() {
        return "Vulcan Low";
    }
}
