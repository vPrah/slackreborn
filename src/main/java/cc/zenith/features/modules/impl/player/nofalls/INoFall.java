package cc.zenith.features.modules.impl.player.nofalls;

import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.events.impl.player.UpdateEvent;

public interface INoFall {
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

    default void onUpdate(UpdateEvent event) {

    }

    ;
}