// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement.speeds.hop;

import cc.slack.events.impl.player.MoveEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;
import cc.slack.utils.player.PlayerUtil;

public class HypixelHopSpeed implements ISpeed {



    @Override
    public void onMove(MoveEvent event) {
        if (mc.getPlayer().onGround) {
            if (MovementUtil.isMoving()) {
                mc.getPlayer().jump();
                MovementUtil.strafe(0.46f);
                mc.getPlayer().motionY = PlayerUtil.getJumpHeight();
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
