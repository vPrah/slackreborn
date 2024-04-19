package cc.slack.features.modules.impl.player.nofalls.specials;


import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.player.nofalls.INoFall;
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VanillaNofall implements INoFall {

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().fallDistance > 2f) {
            PacketUtil.sendNoEvent(new C03PacketPlayer(true));        }
    }

    public String toString() {
        return "Vanilla";
    }
}
