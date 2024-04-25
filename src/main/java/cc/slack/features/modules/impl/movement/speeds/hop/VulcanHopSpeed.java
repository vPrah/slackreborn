// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement.speeds.hop;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;
import net.minecraft.client.settings.GameSettings;

import static net.minecraft.util.MathHelper.abs;

public class VulcanHopSpeed implements ISpeed {

    boolean modifiedTimer;

    @Override
    public void onUpdate(UpdateEvent event) {
        if (modifiedTimer) {
            mc.getTimer().timerSpeed = 1.00f;
            modifiedTimer = false;
        }

        if (MovementUtil.getSpeed() < 0.215f && !mc.getPlayer().onGround) {
            MovementUtil.strafe(0.215f);
        }

        if (mc.getPlayer().onGround && MovementUtil.isMoving()) {
            mc.getPlayer().jump();

            mc.getTimer().timerSpeed = 1.25f;
            modifiedTimer = true;

            MovementUtil.minLimitStrafe(0.4849f);

        } else if (!MovementUtil.isMoving()) {
            mc.getTimer().timerSpeed = 1.00f;
            MovementUtil.resetMotion();
        }
    }

    @Override
    public String toString() {
        return "Vulcan Hop";
    }
}
