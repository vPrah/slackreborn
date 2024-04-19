package cc.zenith.features.modules.impl.player.nofalls.specials;

import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.impl.player.nofalls.INoFall;
import cc.zenith.utils.client.mc;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VerusNofall implements INoFall {


    boolean spoof;

    @Override
    public void onEnable() {
        spoof = false;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().fallDistance - mc.getPlayer().motionY > 3F) {
            mc.getPlayer().motionY = 0.0;
            mc.getPlayer().motionX *= 0.5;
            mc.getPlayer().motionZ *= 0.5;
            mc.getPlayer().fallDistance = 0F;
            spoof = true;
        }
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (spoof && event.getPacket() instanceof C03PacketPlayer) {
            ((C03PacketPlayer) event.getPacket()).onGround = false;
            spoof = false;
        }
    }

    @Override
    public void onMove(MoveEvent event) {

    }

    public String toString() {
        return "Verus";
    }
}
