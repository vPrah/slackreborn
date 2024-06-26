// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement.speeds.hypixel;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.combat.KillAura;
import cc.slack.features.modules.impl.movement.Speed;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.player.MovementUtil;
import cc.slack.utils.player.PlayerUtil;
import cc.slack.utils.rotations.RotationUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class HypixelHopSpeed implements ISpeed {

    int jumpTick = 0;
    boolean wasSlow = false;

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.onGround) {
            if (MovementUtil.isMoving()) {
                wasSlow = false;
                if (jumpTick > 6) jumpTick = 4;
                mc.thePlayer.jump();
                MovementUtil.strafe(0.465f + jumpTick * 0.008f);
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    float amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                    MovementUtil.strafe(0.46f + jumpTick * 0.008f + 0.023f * (amplifier + 1));
                }
                mc.thePlayer.motionY = PlayerUtil.getJumpHeight();
            } else {
                jumpTick = 0;
            }
        } else {
            mc.thePlayer.motionX *= 1.0005;
            mc.thePlayer.motionZ *= 1.0005;

            if (mc.thePlayer.offGroundTicks < 13) {
                if (mc.thePlayer.motionY > 0) {
                    if (jumpTick < 5 && !Slack.getInstance().getModuleManager().getInstance(Speed.class).enabledTime.hasReached(7000)) {
                        mc.timer.timerSpeed = 1.07f + (float) Math.random() * 0.07f;
                    } else {
                        mc.timer.timerSpeed = 1f;
                    }
                } else {
                    mc.timer.timerSpeed = 0.94f;
                }
            } else {
                mc.timer.timerSpeed = 1f;
            }

            if (Slack.getInstance().getModuleManager().getInstance(Speed.class).hypixelTest.getValue()) {
                if (mc.thePlayer.offGroundTicks == 3) {
                    mc.thePlayer.motionY = PlayerUtil.HEAD_HITTER_MOTIONY;
                    mc.timer.timerSpeed = 0.65f;
                }
            }


            if (Slack.getInstance().getModuleManager().getInstance(Speed.class).hypixelSemiStrafe.getValue()) {
                if (mc.thePlayer.offGroundTicks == 6 && Slack.getInstance().getModuleManager().getInstance(KillAura.class).target == null) {
                    if (Math.abs(MathHelper.wrapAngleTo180_float(
                            MovementUtil.getBindsDirection(mc.thePlayer.rotationYaw) -
                                    RotationUtil.getRotations(new Vec3(0, 0, 0), new Vec3(mc.thePlayer.motionX, 0, mc.thePlayer.motionZ))[0]
                    )) > 30) {
                        MovementUtil.strafe(MovementUtil.getSpeed() * 0.86f);
                    }
                }
                return;
//                if (wasSlow) {
//                    MovementUtil.strafe(0.2f);
//                    wasSlow = false;
//                }
//                if (Math.abs(MathHelper.wrapAngleTo180_float(
//                        MovementUtil.getBindsDirection(mc.thePlayer.rotationYaw) -
//                                RotationUtil.getRotations(new Vec3(0, 0, 0), new Vec3(mc.thePlayer.motionX, 0, mc.thePlayer.motionZ))[0]
//                )) > 135) {
//                    MovementUtil.resetMotion(false);
//                    wasSlow = true;
//                }
            }
        }

    }

    @Override
    public String toString() {
        return "Hypixel";
    }
}
