package cc.slack.features.modules.impl.combat.velocitys.impl;

import cc.slack.Slack;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.combat.Velocity;
import cc.slack.features.modules.impl.combat.velocitys.IVelocity;
import cc.slack.utils.player.BlinkUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class HypixelAirVelocity implements IVelocity {

    boolean hypixeltest= false;
    double hypixelY;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = event.getPacket();
            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                Velocity velocityModule = Slack.getInstance().getModuleManager().getInstance(Velocity.class);
                double verticalValue = velocityModule.vertical.getValue().doubleValue();
                if (mc.thePlayer.onGround) {
                    if (hypixeltest) {
                        mc.thePlayer.motionY = hypixelY;
                        BlinkUtil.disable();
                        hypixeltest = false;
                    } else {
                        event.cancel();
                        mc.thePlayer.motionY = packet.getMotionY() * verticalValue / 100 / 8000.0;
                        hypixeltest = false;
                    }
                } else {
                    if (hypixeltest) {
                        event.cancel();
                        hypixelY = packet.getMotionY() * verticalValue / 100 / 8000.0;
                    } else {
                        event.cancel();
                        hypixelY = packet.getMotionY() * verticalValue / 100 / 8000.0;
                        BlinkUtil.enable(true, false);
                        hypixeltest = true;
                    }
                }
            }
        }
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.onGround || mc.thePlayer.ticksSinceLastDamage > 11) {
            if (hypixeltest) {
                mc.thePlayer.motionY = hypixelY;
                BlinkUtil.disable();
                hypixeltest = false;
            }
        }
    }

    @Override
    public String toString() {
        return "Hypixel Air";
    }
}
