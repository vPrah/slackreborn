package cc.zenith.features.modules.impl.player;

import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.network.PacketUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.status.server.S00PacketServerInfo;

import java.util.concurrent.CopyOnWriteArrayList;

@ModuleInfo(
        name = "Blink",
        category = Category.PLAYER
)
public class Blink extends Module {

    private final CopyOnWriteArrayList<Packet> clientPackets = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Packet> serverPackets = new CopyOnWriteArrayList<>();
    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Clientside", "Serverside"});

    public Blink() {
        super();
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        clientPackets.clear();
        serverPackets.clear();
    }

    @Override
    public void onDisable() {
        clientPackets.forEach(packet -> {
            PacketUtil.sendNoEvent(packet);
            clientPackets.remove(packet);
        });

        serverPackets.forEach(packet -> {
            packet.processPacket(mc.getMinecraft().getNetHandler().getNetworkManager().getNetHandler());
            serverPackets.remove(packet);
        });
    }

    @Listen
    public void onPacket(PacketEvent event) {
        switch (event.getDirection()) {
            case OUTGOING:
                if (mode.getValue().equalsIgnoreCase("clientside")) {
                    if (!(event.getPacket() instanceof C00PacketKeepAlive || event.getPacket() instanceof C00Handshake ||
                            event.getPacket() instanceof C00PacketLoginStart)) {
                        clientPackets.add(event.getPacket());
                        event.cancel();
                    }
                }
                break;
            case INCOMING:
                if (mc.getPlayer() == null || mc.getWorld() == null) return;
                if (mode.getValue().equalsIgnoreCase("serverside")) {
                    if (!(event.getPacket() instanceof S00PacketDisconnect ||
                            event.getPacket() instanceof S00PacketServerInfo || event.getPacket() instanceof S3EPacketTeams ||
                            event.getPacket() instanceof S19PacketEntityStatus || event.getPacket() instanceof S02PacketChat ||
                            event.getPacket() instanceof S3BPacketScoreboardObjective)) {
                        serverPackets.add(event.getPacket());
                        event.cancel();
                    }
                }
                break;
        }
    }
}
