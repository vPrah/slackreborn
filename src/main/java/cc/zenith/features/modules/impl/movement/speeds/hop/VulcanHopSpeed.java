package cc.zenith.features.modules.impl.movement.speeds.hop;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.impl.movement.speeds.ISpeed;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.player.MovementUtil;
import net.minecraft.client.settings.GameSettings;

import static net.minecraft.util.MathHelper.abs;

public class VulcanHopSpeed implements ISpeed {

    boolean startTimer;

    @Override
    public void onEnable() {
        startTimer = true;

    }

    @Override
    public void onDisable() {
        startTimer = true;
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (startTimer) {
            mc.getTimer().timerSpeed = 1.00f;
            startTimer = false;
        }
        if (abs(mc.getPlayer().movementInput.moveStrafe) < 0.1f) {
            mc.getPlayer().jumpMovementFactor = 0.026499f;
        }else {
            mc.getPlayer().jumpMovementFactor = 0.0244f;
        }
        mc.getGameSettings().keyBindJump.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindJump);

        if (MovementUtil.getSpeed() < 0.215f && !mc.getPlayer().onGround) {
            MovementUtil.strafe(0.215f);
        }
        if (mc.getPlayer().onGround && MovementUtil.isMoving()) {
            mc.getGameSettings().keyBindJump.pressed = false;
            mc.getPlayer().jump();
            if (!mc.getPlayer().isAirBorne) {
                return;
            }
            mc.getTimer().timerSpeed = 1.25f;
            startTimer = true;
            MovementUtil.strafe();
            if(MovementUtil.getSpeed() < 0.5f) {
                MovementUtil.strafe(0.4849f);
            }
        }else if (!MovementUtil.isMoving()) {
            mc.getTimer().timerSpeed = 1.00f;
            mc.getPlayer().motionX = 0.0D;
            mc.getPlayer().motionZ = 0.0D;
        }
    }

    @Override
    public String toString() {
        return "Vulcan Hop";
    }
}
