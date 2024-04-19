package cc.slack.events.impl.network;

import cc.slack.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.Packet;

@Getter
@AllArgsConstructor
public class PacketEvent extends Event {
    private final Packet packet;
    private final PacketDirection direction;

    public <T extends Packet> T getPacket() {
        return (T) packet;
    }
}
