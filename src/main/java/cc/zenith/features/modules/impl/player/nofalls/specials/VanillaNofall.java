package cc.zenith.features.modules.impl.player.nofalls.specials;


import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.impl.player.nofalls.INoFall;
import cc.zenith.utils.client.MC;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VanillaNofall implements INoFall {

    @Override
    public void onUpdate(UpdateEvent event) {
        if (MC.getPlayer().fallDistance > 2f) {
            MC.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
        }
    }

    public String toString() {
        return "Vanilla";
    }
}
