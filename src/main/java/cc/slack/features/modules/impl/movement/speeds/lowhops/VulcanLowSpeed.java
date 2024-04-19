package cc.slack.features.modules.impl.movement.speeds.lowhops;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;

public class VulcanLowSpeed implements ISpeed {


    double launchY = 0.0D;

    @Override
    public void onEnable() {
    launchY = mc.getPlayer().motionY;
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        mc.getPlayer().jumpMovementFactor = 0.0245f;
        if (mc.getPlayer().onGround && MovementUtil.isMoving()) {
            mc.getPlayer().jump();
            MovementUtil.strafe();
            if (MovementUtil.getSpeed() < 0.5f) {
                MovementUtil.strafe(0.484f);
            }
            launchY = mc.getPlayer().posY;
        } else if (mc.getPlayer().offGroundTicks == 4) {
            mc.getPlayer().motionY = -0.27D;
        }
        if (MovementUtil.getSpeed() < 0.215F && !mc.getPlayer().onGround) {
            MovementUtil.strafe(0.215f);
        }
    }

    @Override
    public String toString() {
        return "Vulcan Low";
    }
}
