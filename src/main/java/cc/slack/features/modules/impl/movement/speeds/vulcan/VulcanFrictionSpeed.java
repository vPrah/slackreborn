package cc.slack.features.modules.impl.movement.speeds.vulcan;


import cc.slack.events.impl.player.MoveEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;
import cc.slack.utils.player.TimerUtil;

public class VulcanFrictionSpeed implements ISpeed {


    private double speed;
    private int ticks;

    @Override
    public void onMove(MoveEvent event) {
        if (mc.getPlayer().onGround) {
            TimerUtil.set(1.0F);
            event.setY(0.41999998688697815);
            speed = 0.6061590433001639;
            ticks = 0;
        } else {
            if (ticks == 0) {
                speed *= 0.5934960376506356;
            } else {
                speed *= 0.989;
            }

            if (ticks == 7) {
                TimerUtil.set(1.2F);
            }

            if (ticks == 9) {
                TimerUtil.set(0.9F);
            }

            ++ticks;
        }
        MovementUtil.setSpeedWithCheck(speed);
    }

    @Override
    public String toString() {
        return "Vulcan Friction";
    }
}
