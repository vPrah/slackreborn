// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement.speeds.vulcan;

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
        if (mc.getPlayer().onGround && MovementUtil.isMoving()) {
            mc.getPlayer().jump();
            MovementUtil.strafe(0.46f);

            launchY = mc.getPlayer().posY;
        } else if(mc.getPlayer().motionY > 0.2) {
            mc.getPlayer().motionY = -0.0784000015258789;
        }
    }

    @Override
    public String toString() {
        return "Vulcan Low";
    }
}
