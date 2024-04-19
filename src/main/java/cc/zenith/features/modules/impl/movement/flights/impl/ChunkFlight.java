package cc.zenith.features.modules.impl.movement.flights.impl;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.impl.movement.flights.IFlight;
import cc.zenith.utils.client.MC;

public class ChunkFlight implements IFlight {

    @Override
    public void onUpdate(UpdateEvent event) {
        if (MC.getPlayer().motionY < 0) {
            MC.getPlayer().motionY = -0.09800000190735147;
        }
    }

    @Override
    public String toString() {
        return "Chunk";
    }
}
