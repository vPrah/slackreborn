package cc.slack.features.modules.impl.utilties;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@ModuleInfo(
        name = "AntiHunger",
        category = Category.UTILITIES
)
public class AntiHunger extends Module {

    @Listen
    public void onPacket (PacketEvent event) {
        final Packet<?> packet = event.getPacket();
        if (packet instanceof C0BPacketEntityAction) {
            C0BPacketEntityAction actionPacket = (C0BPacketEntityAction) packet;
            final C0BPacketEntityAction.Action action = actionPacket.getAction();
            if(action == C0BPacketEntityAction.Action.STOP_SPRINTING || action == C0BPacketEntityAction.Action.START_SPRINTING) {
                event.cancel();
            }
        }
    }

}
