// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.player.nofalls.specials;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MoveEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.player.nofalls.INoFall;
import cc.slack.utils.client.mc;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VerusNofall implements INoFall {


    boolean spoof;
    int packet1Count;
    boolean packetModify;

    @Override
    public void onEnable() {
        packetModify = false;
        packet1Count = 0;
        spoof = false;
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().fallDistance - mc.getPlayer().motionY > 3) {
            mc.getPlayer().motionY = 0.0D;
            mc.getPlayer().motionX *= 0.5D;
            mc.getPlayer().motionZ *= 0.5D;
            mc.getPlayer().fallDistance = 0F;
            spoof = true;
        }
        if (mc.getPlayer().fallDistance / 3 > packet1Count) {
            packet1Count = (int) (mc.getPlayer().fallDistance / 3);
            packetModify = true;
        }
        if (mc.getPlayer().onGround) {
            packet1Count = 0;
        }

    }

    @Override
    public void onPacket(PacketEvent event) {
        if (spoof && event.getPacket() instanceof C03PacketPlayer) {
            ((C03PacketPlayer) event.getPacket()).onGround = true;
            spoof = false;
        }
    }

    public String toString() {
        return "Verus";
    }
}
