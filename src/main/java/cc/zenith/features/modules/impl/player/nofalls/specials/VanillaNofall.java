package cc.zenith.features.modules.impl.player.nofalls.specials;


import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.impl.player.nofalls.INoFall;
import cc.zenith.utils.client.mc;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VanillaNofall implements INoFall {

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().fallDistance > 2f) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
        }
    }

    public String toString() {
        return "Vanilla";
    }
}
