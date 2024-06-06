// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement.speeds.hypixel;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.Speed;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;
import cc.slack.utils.player.PlayerUtil;
import cc.slack.utils.rotations.RotationUtil;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class HypixelBasicSpeed implements ISpeed {

    int jumpTick = 0;

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().onGround) {
            if (MovementUtil.isMoving()) {
                if (jumpTick > 6) jumpTick = 4;
                mc.getPlayer().jump();
                MovementUtil.strafe(0.45f + jumpTick * 0.0033f);
                if (mc.getPlayer().isPotionActive(Potion.moveSpeed)) {
                    float amplifier = mc.getPlayer().getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                    MovementUtil.strafe(0.46f + jumpTick * 0.005f + 0.024f * (amplifier + 1));
                }
                mc.getPlayer().motionY = PlayerUtil.getJumpHeight();
            } else {
                jumpTick = 0;
            }
        } else {
            mc.getPlayer().motionX *= 1.0005;
            mc.getPlayer().motionZ *= 1.0005;
        }

    }

    @Override
    public String toString() {
        return "Hypixel Basic";
    }
}
