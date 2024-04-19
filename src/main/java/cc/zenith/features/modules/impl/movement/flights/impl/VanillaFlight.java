package cc.zenith.features.modules.impl.movement.flights.impl;

import cc.zenith.Zenith;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.features.modules.impl.movement.Flight;
import cc.zenith.features.modules.impl.movement.flights.IFlight;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.player.MovementUtil;

public class VanillaFlight implements IFlight {

    double speed = 0.0;

    @Override
    public void onDisable() {
        MovementUtil.resetMotion(true);
    }

    @Override
    public void onMove(MoveEvent event) {
        speed = Zenith.getInstance().getModuleManager().getInstance(Flight.class).vanillaspeed.getValue();
        event.setY((mc.getGameSettings().keyBindJump.isKeyDown() ? 1 * speed :
                mc.getGameSettings().keyBindSneak.isKeyDown() ? -1 * speed : 0));
        MovementUtil.setSpeed(event, speed);
    }

    @Override
    public String toString() {
        return "Vanilla";
    }
}
