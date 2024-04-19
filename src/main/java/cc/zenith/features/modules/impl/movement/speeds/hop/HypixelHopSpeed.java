package cc.zenith.features.modules.impl.movement.speeds.hop;

import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.features.modules.impl.movement.speeds.ISpeed;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.player.MovementUtil;
import cc.zenith.utils.player.PlayerUtil;

public class HypixelHopSpeed implements ISpeed {



    @Override
    public void onMove(MoveEvent event) {
        if (mc.getPlayer().onGround) {
            if (MovementUtil.isMoving()) {
                event.setY((float) PlayerUtil.BASE_JUMP_HEIGHT);
                if (MovementUtil.getSpeed(event) < 0.46) {
                    MovementUtil.setSpeed(event, 0.46f);
                } else {
                    MovementUtil.setSpeed(event, MovementUtil.getSpeed(event));
                }
            }
        } else {
        }

    }

    @Override
    public String toString() {
        return "Hypixel Hop";
    }
}
