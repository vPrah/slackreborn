package cc.zenith.features.modules.impl.movement.speeds.yport;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.impl.movement.speeds.ISpeed;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.player.MovementUtil;

public class VulcanLowSpeed implements ISpeed {


    int ticks;
    double launchY = 0.0D;

    @Override
    public void onEnable() {
    ticks = 0;
    launchY = mc.getPlayer().motionY;
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        ticks++;
        mc.getPlayer().jumpMovementFactor = 0.0245f;
        if (mc.getPlayer().onGround && MovementUtil.isMoving()) {
            mc.getPlayer().jump();
            ticks = 0;
            MovementUtil.strafe();
            if (MovementUtil.getSpeed() < 0.5f) {
                MovementUtil.strafe(0.484f);
            }
            launchY = mc.getPlayer().posY;
        }else if (mc.getPlayer().posY > launchY && ticks <= 1) {
            mc.getPlayer().setPosition(mc.getPlayer().posX, launchY, mc.getPlayer().posZ);
        }else if (ticks == 5) {
            mc.getPlayer().motionY = -0.17D;
        }
        if (MovementUtil.getSpeed() < 0.215F) {
            MovementUtil.strafe(0.215f);
        }
    }

    @Override
    public String toString() {
        return "Vulcan Low";
    }
}
