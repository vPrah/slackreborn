// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement.speeds.vanilla;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.Speed;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;

public class VanillaSpeed implements ISpeed {

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().onGround && MovementUtil.isMoving()) {
            mc.getPlayer().jump();
            MovementUtil.strafe(Slack.getInstance().getModuleManager().getInstance(Speed.class).vanillaspeed.getValue());
        }
        MovementUtil.strafe();
    }

    @Override
    public String toString() {
        return "Vanilla Hop";
    }
}
