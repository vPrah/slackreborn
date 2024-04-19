package cc.slack.utils.network;

import net.minecraft.network.Packet;

@Deprecated // Create Timed Packet In Packet Utils Make Use TimeUil
public class TimedPacket {

    private Packet<?> packet;
    private long ms;

    public TimedPacket(Packet<?> packet, long ms) {
        this.packet = packet;
        this.ms = ms;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public long getMs() {
        return ms;
    }

    public void setMs(long ms) {
        this.ms = ms;
    }

    public boolean elapsed(long ms) {
        return System.currentTimeMillis() - getMs() > ms;
    }
}
