package cc.slack.features.modules.impl.movement.spiders.impl;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.impl.movement.spiders.ISpider;
import cc.slack.utils.player.MovementUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

public class VulcanSpider implements ISpider {

    private int vulcanTicks;

    @Override
    public void onMotion(MotionEvent event) {
        if (mc.thePlayer.isCollidedHorizontally) {
            if (mc.thePlayer.onGround) {
                vulcanTicks = 0;
                mc.thePlayer.jump();
            }
            if (vulcanTicks >= 3) {
                vulcanTicks = 0;
            }
            vulcanTicks++;
            switch (vulcanTicks) {
                case 2:
                case 3:
                    mc.thePlayer.jump();
                    MovementUtil.resetMotion(false);
            }
        }
    }

    @Override
    public void onPacket(PacketEvent event) {
        Packet packet = event.getPacket();
        if (packet instanceof C03PacketPlayer) {
            if (mc.thePlayer.isCollidedHorizontally) {
                switch (vulcanTicks) {
                    case 2:
                        ((C03PacketPlayer) packet).onGround = true;
                        break;
                    case 3:
                        ((C03PacketPlayer) packet).y -= 0.05;
                        ((C03PacketPlayer) packet).x -= MathHelper.sin((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.1;
                        ((C03PacketPlayer) packet).z += MathHelper.cos((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.1;
                }
                event.setPacket(packet);
            }
        }
    }

    @Override
    public void onDisable() {
        vulcanTicks = 0;
    }

    @Override
    public String toString() {
        return "Vulcan";
    }
}
