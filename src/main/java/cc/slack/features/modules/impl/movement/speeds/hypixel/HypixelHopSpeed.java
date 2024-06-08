// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement.speeds.hypixel;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.Speed;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.player.MovementUtil;
import cc.slack.utils.player.PlayerUtil;
import cc.slack.utils.rotations.RotationUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class HypixelHopSpeed implements ISpeed {

    int jumpTick = 0;
    boolean wasSlow = false;

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().onGround) {
            if (MovementUtil.isMoving()) {
                wasSlow = false;
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

            if (mc.getPlayer().motionY > 0) {
                mc.getTimer().timerSpeed = 0.94f;
            } else if (mc.getPlayer().offGroundTicks < 13) {
                if (jumpTick < 5 && !Slack.getInstance().getModuleManager().getInstance(Speed.class).enabledTime.hasReached(7000)) {
                    mc.getTimer().timerSpeed = 1.06f + (float) Math.random() * 0.07f;
                } else {
                    mc.getTimer().timerSpeed = 1f;
                }
            }

            if (Slack.getInstance().getModuleManager().getInstance(Speed.class).hypixelTest.getValue()) {
                if (mc.getPlayer().offGroundTicks == 5) {
                    PacketUtil.send(new C03PacketPlayer(false));
                    mc.getPlayer().motionY = PlayerUtil.HEAD_HITTER_MOTIONY;
                }
            }


            if (Slack.getInstance().getModuleManager().getInstance(Speed.class).hypixelSemiStrafe.getValue()) {
                if (mc.getPlayer().offGroundTicks == 4) {
                    if (Math.abs(MathHelper.wrapAngleTo180_float(
                            MovementUtil.getBindsDirection(mc.getPlayer().rotationYaw) -
                                    RotationUtil.getRotations(new Vec3(0, 0, 0), new Vec3(mc.getPlayer().motionX, 0, mc.getPlayer().motionZ))[0]
                    )) > 30) {
                        MovementUtil.strafe(MovementUtil.getSpeed() * 0.8f);
                    }
                }
                return;
//                if (wasSlow) {
//                    MovementUtil.strafe(0.2f);
//                    wasSlow = false;
//                }
//                if (Math.abs(MathHelper.wrapAngleTo180_float(
//                        MovementUtil.getBindsDirection(mc.getPlayer().rotationYaw) -
//                                RotationUtil.getRotations(new Vec3(0, 0, 0), new Vec3(mc.getPlayer().motionX, 0, mc.getPlayer().motionZ))[0]
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
