package cc.zenith.features.modules.impl.movement.flights.impl;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.impl.movement.flights.IFlight;
import cc.zenith.utils.client.mc;

public class ChunkFlight implements IFlight {

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().motionY < 0) {
            mc.getPlayer().motionY = -0.09800000190735147;
        }
    }

    @Override
    public String toString() {
        return "Chunk";
    }
}
