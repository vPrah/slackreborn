package cc.zenith.features.modules.impl.movement.speeds;

import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.CollideEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.events.impl.player.UpdateEvent;

public interface ISpeed {
    default void onEnable() {
    }

    ;

    default void onDisable() {
    }

    ;

    default void onMove(MoveEvent event) {
    }

    ;

    default void onPacket(PacketEvent event) {
    }

    ;

    default void onUpdate(UpdateEvent event) {
    }

    ;
}
