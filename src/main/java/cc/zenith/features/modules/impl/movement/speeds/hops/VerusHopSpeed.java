package cc.zenith.features.modules.impl.movement.speeds.hops;

import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.features.modules.impl.movement.speeds.ISpeed;
import cc.zenith.utils.client.MC;
import cc.zenith.utils.player.MoveUtil;
import cc.zenith.utils.player.PlayerUtil;

public class VerusHopSpeed implements ISpeed {

    double moveSpeed;
    int airTicks;

    @Override
    public void onEnable() {
        moveSpeed = 0;
        airTicks = 0;
    }

    @Override
    public void onMove(MoveEvent event) {
        if (MC.getPlayer().onGround) {
            if (MoveUtil.isMoving()) event.setY(0.42F);
            moveSpeed = 0.68f;
            airTicks = 0;
        } else {
            if (airTicks == 0)
                moveSpeed *= PlayerUtil.BASE_GROUND_FRICTION * 1.01;

            airTicks++;
        }

        MoveUtil.setSpeed(event, moveSpeed);
        moveSpeed *= PlayerUtil.MOVE_FRICTION;
    }

    @Override
    public String toString() {
        return "Verus Hop";
    }
}
