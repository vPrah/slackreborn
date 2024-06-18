// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.other.PrintUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.status.server.S00PacketServerInfo;

import java.util.ArrayList;

@ModuleInfo(
        name = "Backtrack",
        category = Category.GHOST
)
public class Backtrack extends Module {

    private final NumberValue<Integer> maxDelay = new NumberValue<>("Max Delay", 4, 1, 40, 1);
    private final NumberValue<Integer> backtrackTime = new NumberValue<>("Backtrack ticks", 20, 10, 60, 1);

    private int ticksSinceAttack = 0;
    private int backtrackTicks = 0;
    private boolean enabled = false;
    private boolean releasing = false;
    public EntityPlayer player;

    ArrayList<ArrayList<Packet>> packetCache = new ArrayList<>();
    ArrayList<Packet> currentTick = new ArrayList<>();

    public Backtrack() {
        addSettings(maxDelay, backtrackTime);
    }

    @Override
    public void onEnable() {
        ticksSinceAttack = 0;
        backtrackTicks = 0;
        enabled = false;
        packetCache.clear();
        currentTick.clear();
    }

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {
        if (mc.thePlayer == null || mc.getWorld() == null) {
            enabled = false;
            currentTick.clear();
            packetCache.clear();
        }
        if (!currentTick.isEmpty()) {
            packetCache.add(currentTick);
            currentTick.clear();
        }
        if (enabled) {
            if (ticksSinceAttack < maxDelay.getValue()) {
                ticksSinceAttack ++;
            } else {
                PrintUtil.message("release");
                releaseFirst();
            }
        }
        if (backtrackTicks > 0) {
            backtrackTicks --;
        } else {
            if (enabled) {
                int cacheSize = packetCache.size();
                for (int i = 0; i < cacheSize; i ++ ) {
                    releaseFirst();
                }
                enabled = false;
                currentTick.clear();
                packetCache.clear();
            }
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        final Packet packet = event.getPacket();

        if (event.getDirection() == PacketDirection.OUTGOING) {
            if (event.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity wrapper = (C02PacketUseEntity) packet;
                if (wrapper.getEntityFromWorld(mc.getWorld()) instanceof EntityPlayer && wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
                    if (backtrackTicks == 0) ticksSinceAttack = 0;
                    backtrackTicks = backtrackTime.getValue();
                    enabled = true;
                    player = (EntityPlayer) wrapper.getEntityFromWorld(mc.getWorld());
                }
            }
        } else {
            if (!(packet instanceof S00PacketDisconnect ||
                    packet instanceof S00PacketServerInfo || packet instanceof S3EPacketTeams ||
                    packet instanceof S19PacketEntityStatus || packet instanceof S02PacketChat ||
                    packet instanceof S3BPacketScoreboardObjective) && mc.thePlayer.ticksExisted > 4) {
                if (enabled && !releasing) {
                    currentTick.add(packet);
                    event.cancel();
                }
            }
        }
    }

    private void releaseFirst() {
        if (packetCache.isEmpty()) return;
        releasing = true;
        ArrayList<Packet> first = packetCache.get(0);
        for (Packet packet : first) {
            try {
                PrintUtil.message("release" + packet.toString());
                packet.processPacket(mc.getMinecraft().getNetHandler().getNetworkManager().getNetHandler());
            } catch ( Exception e) {
                PrintUtil.message("Failed to process packet: " + packet.toString());
            }
        }
        releasing = false;
        packetCache.remove(0);
    }
}
