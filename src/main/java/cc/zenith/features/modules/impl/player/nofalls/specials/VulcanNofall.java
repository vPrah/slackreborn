package cc.zenith.features.modules.impl.player.nofalls.specials;

import cc.zenith.Zenith;
import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.MotionEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.impl.movement.Flight;
import cc.zenith.features.modules.impl.player.nofalls.INoFall;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.network.PacketUtil;
import cc.zenith.utils.player.PlayerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VulcanNofall implements INoFall {

    int count;
    boolean isFixed;

    @Override
    public void onEnable() {
        count = 0;
        isFixed = false;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onUpdate(UpdateEvent event) {
//        if (Zenith.getInstance().getModuleManager().getInstance(Flight.class).isToggle()) return;
    }

    @Override
    public void onPacket(PacketEvent event) {

    }

    @Override
    public void onMove(MoveEvent event) {

    }

    @Override
    public void onMotion(MotionEvent event) {
        if (mc.getPlayer().onGround && isFixed) {
            isFixed = false;
            count = 0;
            mc.getTimer().timerSpeed = 1.0F;
        }

        if (mc.getPlayer().fallDistance > 2.0F) {
            isFixed = true;
            mc.getTimer().timerSpeed = 0.9F;
        }

        if (mc.getPlayer().fallDistance > 2.9F) {
            PacketUtil.sendNoEvent(new C03PacketPlayer(true));
            mc.getPlayer().motionY = -0.1D;
            mc.getPlayer().fallDistance = 0;
            mc.getPlayer().motionY *= 1.1D;

            if (count++ > 5)
                count = 0;
        }
    }

    public String toString() {
        return "Vulcan";
    }
}
