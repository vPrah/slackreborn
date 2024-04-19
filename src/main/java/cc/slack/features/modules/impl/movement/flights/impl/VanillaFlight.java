package cc.slack.features.modules.impl.movement.flights.impl;

import cc.slack.Slack;
import cc.slack.events.impl.player.MoveEvent;
import cc.slack.features.modules.impl.movement.Flight;
import cc.slack.features.modules.impl.movement.flights.IFlight;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;

public class VanillaFlight implements IFlight {

    double speed = 0.0;

    @Override
    public void onDisable() {
        MovementUtil.resetMotion(true);
    }

    @Override
    public void onMove(MoveEvent event) {
        speed = Slack.getInstance().getModuleManager().getInstance(Flight.class).vanillaspeed.getValue();
        event.setY((mc.getGameSettings().keyBindJump.isKeyDown() ? 1 * speed :
                mc.getGameSettings().keyBindSneak.isKeyDown() ? -1 * speed : 0));
        MovementUtil.setSpeed(event, speed);
    }

    @Override
    public String toString() {
        return "Vanilla";
    }
}
