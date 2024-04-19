package cc.zenith.features.modules.impl.player.nofalls.specials;

import cc.zenith.Zenith;
import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.impl.movement.Flight;
import cc.zenith.features.modules.impl.player.nofalls.INoFall;
import cc.zenith.utils.client.MC;
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
        if (Zenith.getInstance().getModuleManager().getInstance(Flight.class).isToggle()) return;

        if (MC.getPlayer().onGround && isFixed) {
            isFixed = false;
            count = 0;
            MC.getTimer().timerSpeed = 1.0F;
        }

        if (MC.getPlayer().fallDistance > 2.0F) {
            isFixed = true;
            MC.getTimer().timerSpeed = 0.75F;
        }

        if (MC.getPlayer().fallDistance > 2.94F && PlayerUtil.isBlockUnder()) {
            PacketUtil.sendNoEvent(new C03PacketPlayer(true));
            MC.getPlayer().motionY = -0.1F;
            MC.getPlayer().fallDistance = 0;
            MC.getPlayer().motionY *= 1.1F;

            if (count++ > 5)
                count = 0;
        }
    }

    @Override
    public void onPacket(PacketEvent event) {

    }

    @Override
    public void onMove(MoveEvent event) {

    }

    public String toString() {
        return "Vulcan";
    }
}
