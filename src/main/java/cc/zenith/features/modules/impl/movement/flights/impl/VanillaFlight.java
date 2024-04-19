package cc.zenith.features.modules.impl.movement.flights.impl;

import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.features.modules.impl.movement.flights.IFlight;
import cc.zenith.utils.client.MC;
import cc.zenith.utils.player.MoveUtil;

public class VanillaFlight implements IFlight {

    @Override
    public void onDisable() {
        MoveUtil.resetMotion(true);
    }

    @Override
    public void onMove(MoveEvent event) {
        event.setY((MC.getGameSettings().keyBindJump.isKeyDown() ? 1 * 3.32 :
                MC.getGameSettings().keyBindSneak.isKeyDown() ? -1 * 3.32 : 0));
        MoveUtil.setSpeed(event, 3.5);
    }

    @Override
    public String toString() {
        return "Vanilla";
    }
}
