// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement.speeds.vanilla;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.other.PrintUtil;
import cc.slack.utils.player.MovementUtil;

public class LegitSpeed implements ISpeed {

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.onGround && MovementUtil.isMoving()) {
            mc.thePlayer.jump();
            PrintUtil.message(Float.toString(MovementUtil.getSpeed()));
        } else if (mc.thePlayer.offGroundTicks == 1) {
            PrintUtil.message(Float.toString(MovementUtil.getSpeed()));
        }
    }

    @Override
    public String toString() {
        return "Legit";
    }
}
