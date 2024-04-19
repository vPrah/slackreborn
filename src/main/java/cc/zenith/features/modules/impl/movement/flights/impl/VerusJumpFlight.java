package cc.zenith.features.modules.impl.movement.flights.impl;

import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.CollideEvent;
import cc.zenith.events.impl.player.MotionEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.impl.movement.flights.IFlight;
import cc.zenith.utils.client.MC;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class VerusJumpFlight implements IFlight {

    double startY;

    @Override
    public void onEnable() {
        startY = Math.floor(MC.getPlayer().posY);
    }

    @Override
    public void onPacket(PacketEvent event) {
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (MC.getPlayer().onGround) {
            MC.getPlayer().jump();
        }
        MC.getGameSettings().keyBindJump.pressed = false;
    }

    @Override
    public void onCollide(CollideEvent event) {
        if (event.getBlock() instanceof BlockAir && event.getY() <= startY)
            event.setBoundingBox(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1, startY, event.getZ() + 1));
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onMotion(MotionEvent event) {

    }

    @Override
    public String toString() {
        return "Verus Jump";
    }
}
