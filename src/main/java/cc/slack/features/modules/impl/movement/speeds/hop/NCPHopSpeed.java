package cc.slack.features.modules.impl.movement.speeds.hop;

import cc.slack.events.impl.player.MoveEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;
import cc.slack.utils.player.PlayerUtil;
import net.minecraft.potion.Potion;

public class NCPHopSpeed implements ISpeed {



    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().onGround) {
            if (MovementUtil.isMoving()) {
                mc.getPlayer().jump();
                double baseSpeed = 0.484;
                if (mc.getPlayer().isPotionActive(Potion.moveSpeed)) {
                    double amplifier = mc.getPlayer().getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                    baseSpeed *= 1.0 + 0.13 * (amplifier + 1);
                }
                MovementUtil.minLimitStrafe((float) baseSpeed);

            }
        } else {
            mc.getPlayer().motionX *= 1.001;
            mc.getPlayer().motionZ *= 1.001;

            if (mc.getPlayer().offGroundTicks == 5) {
                mc.getPlayer().motionY = PlayerUtil.HEAD_HITTER_MOTIONY;
            }
            MovementUtil.strafe();
        }

    }

    @Override
    public String toString() {
        return "NCP Hop";
    }
}
