package cc.zenith.features.modules.impl.movement.flights.impl;

import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.features.modules.impl.movement.flights.IFlight;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.player.MovementUtil;

public class VanillaFlight implements IFlight {

    @Override
    public void onDisable() {
        MovementUtil.resetMotion(true);
    }

    @Override
    public void onMove(MoveEvent event) {
        event.setY((mc.getGameSettings().keyBindJump.isKeyDown() ? 1 * 3.32 :
                mc.getGameSettings().keyBindSneak.isKeyDown() ? -1 * 3.32 : 0));
        MovementUtil.setSpeed(event, 3.5);
    }

    @Override
    public String toString() {
        return "Vanilla";
    }
}
