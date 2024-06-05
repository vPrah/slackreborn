package cc.slack.features.modules.impl.movement.speeds.vulcan;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;
import net.minecraft.client.settings.GameSettings;

public class VulcanPortSpeed implements ISpeed {

    private boolean wasTimer = false;
    private int ticks = 0;

    @Override
    public void onEnable() {
        wasTimer = true;
        ticks = 0;
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        ticks++;
        if (wasTimer) {
            mc.getTimer().timerSpeed = 1.00f;
            wasTimer = false;
        }
        mc.getPlayer().jumpMovementFactor = 0.0245f;
        if (!mc.getPlayer().onGround && ticks > 3 && mc.getPlayer().motionY > 0) {
            mc.getPlayer().motionY = -0.27;
        }

        mc.getGameSettings().keyBindJump.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindJump);
        if (MovementUtil.getSpeed() < 0.215f && !mc.getPlayer().onGround) {
            MovementUtil.strafe(0.215f);
        }
        if (mc.getPlayer().onGround && MovementUtil.isMoving()) {
            ticks = 0;
            mc.getGameSettings().keyBindJump.pressed = false;
            mc.getPlayer().jump();
            if (!mc.getPlayer().isAirBorne) {
                return;
            }
            mc.getTimer().timerSpeed = 1.2f;
            wasTimer = true;
            if(MovementUtil.getSpeed() < 0.48f) {
                MovementUtil.strafe(0.48f);
            }else{
                MovementUtil.strafe((float) (MovementUtil.getSpeed() *0.985));
            }
        }else if (!MovementUtil.isMoving()) {
            mc.getTimer().timerSpeed = 1.00f;
            mc.getPlayer().motionX = 0.0;
            mc.getPlayer().motionZ = 0.0;
        }
    }

    @Override
    public String toString() {
        return "Vulcan Port";
    }
}
