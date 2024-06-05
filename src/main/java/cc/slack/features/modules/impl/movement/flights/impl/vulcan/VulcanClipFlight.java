package cc.slack.features.modules.impl.movement.flights.impl.vulcan;

import cc.slack.Slack;
import cc.slack.events.State;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.impl.movement.Flight;
import cc.slack.features.modules.impl.movement.flights.IFlight;
import cc.slack.utils.client.mc;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class VulcanClipFlight implements IFlight {

    private boolean waitFlag = false;
    private boolean canGlide = false;
    private int ticks = 0;

    @Override
    public void onEnable() {
        if(mc.getPlayer().onGround && Slack.getInstance().getModuleManager().getInstance(Flight.class).vulcanClip.getValue()) {
            clip(0f, -0.1f);
            waitFlag = true;
            canGlide = false;
            ticks = 0;
            mc.getTimer().timerSpeed = 0.1f;
        } else {
            waitFlag = false;
            canGlide = true;
        }
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (event.getState() == State.PRE && canGlide) {
            mc.getTimer().timerSpeed = 1f;
            mc.getPlayer().motionY = (ticks % 2 == 0) ? -0.17 : -0.10;
            if(ticks == 0) {
                mc.getPlayer().motionY = -0.07;
            }
            ticks++;
        }

        if (ticks > 4 && mc.getPlayer().onGround) Slack.getInstance().getModuleManager().getInstance(Flight.class).toggle();
    }

    @Override
    public void onPacket(PacketEvent event) {
        if(event.getPacket() instanceof S08PacketPlayerPosLook && waitFlag) {
            waitFlag = false;
            mc.getPlayer().setPosition(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY(), ((S08PacketPlayerPosLook) event.getPacket()).getZ());
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.getPlayer().posX, mc.getPlayer().posY, mc.getPlayer().posZ, mc.getPlayer().rotationYaw, mc.getPlayer().rotationPitch, false));
            event.cancel();
            mc.getPlayer().jump();
            clip(0.127318f, 0f);
            clip(3.425559f, 3.7f);
            clip(3.14285f, 3.54f);
            clip(2.88522f, 3.4f);
            canGlide = true;
        }
    }

    @Override
    public void onDisable() {
        mc.getTimer().timerSpeed = 1F;
    }

    private void clip(float dist, float y) {
        double yaw = Math.toRadians(mc.getPlayer().rotationYaw);
        double x = -Math.sin(yaw) * dist;
        double z = Math.cos(yaw) * dist;
        mc.getPlayer().setPosition(mc.getPlayer().posX + x, mc.getPlayer().posY + y, mc.getPlayer().posZ + z);
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY, mc.getPlayer().posZ, false));
    }

    @Override
    public String toString() {
        return "Vulcan Clip";
    }
}