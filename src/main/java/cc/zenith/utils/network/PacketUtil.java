package cc.zenith.utils.network;

import cc.zenith.utils.client.MC;
import net.minecraft.network.Packet;

public final class PacketUtil extends MC {

    public static void send(Packet<?> packet) {
        getNetHandler().getNetworkManager().sendPacket(packet);
    }

    public static void send(Packet<?> packet, int iterations) {
        for (int i = 0; i < iterations; i++) send(packet);
    }

    public static void sendNoEvent(Packet<?> packet) {
        getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
    }

    public static void sendNoEvent(Packet<?> packet, int iterations) {
        for (int i = 0; i < iterations; i++) sendNoEvent(packet);
    }
}
