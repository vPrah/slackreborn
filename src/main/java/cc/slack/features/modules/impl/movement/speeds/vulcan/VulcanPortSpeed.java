package cc.slack.features.modules.impl.movement.speeds.vulcan;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;
import net.minecraft.client.settings.GameSettings;

public class VulcanPortSpeed implements ISpeed {


    @Override
    public void onUpdate(UpdateEvent event) {
        mc.getPlayer().jumpMovementFactor = 0.0245f;
        if (!mc.getPlayer().onGround && mc.getPlayer().offGroundTicks > 3 && mc.getPlayer().motionY > 0) {
            mc.getPlayer().motionY = -0.27;
        }

        if (MovementUtil.getSpeed() < 0.215f && !mc.getPlayer().onGround) {
            MovementUtil.strafe(0.215f);
        }

        if (mc.getPlayer().onGround && MovementUtil.isMoving()) {
            mc.getPlayer().jump();
            if (MovementUtil.getSpeed() < 0.48f) {
                MovementUtil.strafe(0.48f);
            } else {
                MovementUtil.strafe();
            }
        } else if (!MovementUtil.isMoving()) {
            MovementUtil.resetMotion();
        }
    }

    @Override
    public String toString() {
        return "Vulcan Port";
    }
}
