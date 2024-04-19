package cc.zenith.features.modules.impl.movement.flights.impl;

import cc.zenith.Zenith;
import cc.zenith.events.State;
import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.MotionEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.features.modules.impl.movement.Flight;
import cc.zenith.features.modules.impl.movement.flights.IFlight;
import cc.zenith.utils.client.MC;
import cc.zenith.utils.player.MoveUtil;
import cc.zenith.utils.player.TimerUtil;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VerusFlight implements IFlight {

    double moveSpeed = 0.0;

    int stage = 0;
    int hops = 0;
    int ticks = 0;

    @Override
    public void onEnable() {
        stage = -1;
        hops = 0;
        ticks = 0;
    }

    @Override
    public void onMove(MoveEvent event) {
        switch (stage) {
            case -1:
                stage++;
                break;
            case 0:
                event.setZeroXZ();

                if (hops >= 4 && MC.getPlayer().onGround) {
                    stage++;
                    return;
                }

                if (MC.getPlayer().onGround) {
                    event.setY(0.42f);
                    ticks = 0;
                    hops++;
                } else {
                    ticks++;
                }
                break;
            case 1:
                TimerUtil.reset();
                event.setZeroXZ();

                if (MC.getPlayer().hurtTime > 0) {
                    ticks = 0;
                    moveSpeed = 0.525;
                    stage++;
                    event.setY(0.42f);
                    MoveUtil.setSpeed(event, moveSpeed);
                }
                break;
            case 2:
                if (event.getY() < 0)
                    event.setY(-0.033);

                if (ticks == 0) moveSpeed *= 7;

                moveSpeed -= moveSpeed / 159.0;
                ticks++;

                MoveUtil.setSpeed(event, moveSpeed);

                if (MC.getPlayer().hurtTime == 0 && (MC.getPlayer().onGround || MC.getPlayer().isCollidedHorizontally))
                    Zenith.getInstance().getModuleManager().getInstance(Flight.class).toggle();
                break;
        }
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (event.getState() != State.PRE) return;
        event.setYaw(MoveUtil.getDirection());
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getDirection() != PacketDirection.OUTGOING) return;
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (stage == 0 && hops >= 1) {
                ((C03PacketPlayer) event.getPacket()).onGround = false;
            }
        }
    }

    @Override
    public String toString() {
        return "Verus";
    }
}
