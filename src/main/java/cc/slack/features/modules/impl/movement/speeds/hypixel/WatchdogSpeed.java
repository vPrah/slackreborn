package cc.slack.features.modules.impl.movement.speeds.hypixel;

import cc.slack.events.impl.player.MoveEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;
import net.minecraft.potion.Potion;


public class WatchdogSpeed implements ISpeed {

    private SpeedState state;
    private int hops;
    private double motion;
    private int airTicks;

    @Override
    public void onEnable() {
        state = SpeedState.IDLING;
        hops = 0;
    }

    @Override
    public void onMove(MoveEvent event) {
        if (mc.getCurrentScreen() == null) {
            double boost = 0.6013831405863909;
            if (mc.getPlayer().isPotionActive(Potion.moveSpeed)) {
                boost *= 1.0 + (double)(mc.getPlayer().getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * 0.15;
            }

            if (state == SpeedState.IDLING) {
                if (mc.getPlayer().hurtTime > 0 && !mc.getPlayer().onGround) {
                    state = SpeedState.AIR;
                }

                event.setX(event.getX() * 0.5);
                event.setZ(event.getZ() * 0.5);
                motion = boost * 0.98;
            }

            if (mc.getPlayer().onGround) {
                state = SpeedState.GROUND;
                airTicks = 0;
            } else {
                if (state == SpeedState.BOOSTING) {
                    state = SpeedState.AIR;
                }

                if (state == SpeedState.GROUND) {
                    state = SpeedState.BOOSTING;
                }

                ++airTicks;
            }

            switch (state) {
                case GROUND:
                    double delta = boost - motion;
                    motion += delta / 5.0;
                    event.setY(0.41999998688697815);
                    MovementUtil.setSpeed(event, motion);
                    ++hops;
                    if (hops >= 7) {
                        motion = boost * 0.96;
                        hops = 0;
                    }
                    break;
                case BOOSTING:
                    event.setX(event.getX() * 1.010);
                    event.setZ(event.getZ() * 1.010);
                    break;
                case AIR:
                    if (mc.getPlayer().hurtTime >= 9) {
                        MovementUtil.setSpeed(event, boost * 0.15);
                    } else if (mc.getPlayer().hurtTime > 0) {
                        MovementUtil.setSpeed(event, MovementUtil.getSpeed(event));
                    } else if (airTicks == 4 || airTicks == 6) {
                        event.setX(event.getX() * 1.06);
                        event.setZ(event.getZ() * 1.06);
                    }
            }
        }
    }

    static enum SpeedState {
        IDLING,
        GROUND,
        BOOSTING,
        AIR,
        DAMAGED;

        private SpeedState() {
        }
    }


    @Override
    public String toString() {
        return "Watchdog";
    }
}
