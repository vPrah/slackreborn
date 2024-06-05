// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement.flights.impl.vanilla;

import cc.slack.Slack;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.movement.Flight;
import cc.slack.features.modules.impl.movement.flights.IFlight;
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.player.InventoryUtil;
import cc.slack.utils.player.MovementUtil;
import cc.slack.utils.rotations.RotationUtil;
import net.minecraft.item.ItemFireball;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class FireballFlight implements IFlight {

    private boolean sent = false;
    private boolean reset = false;
    private boolean gotVelo = false;

    private int fireballSlot = 0;

    @Override
    public void onEnable() {
        sent = false;
        reset = false;
        gotVelo = false;
        fireballSlot = InventoryUtil.findFireball();
        if (fireballSlot == -1) {
            Slack.getInstance().addNotification("Fireball needed to fly", "", 3000L, Slack.NotificationStyle.WARN);
            Slack.getInstance().getModuleManager().getInstance(Flight.class).setToggle(false);
        }
    }

    @Override
    public void onDisable() {
        if (sent && !reset) {
            PacketUtil.send(new C09PacketHeldItemChange(mc.getPlayer().inventory.currentItem));
        }
    }


    @Override
    public void onUpdate(UpdateEvent event) {
        if (!sent) {
            if (mc.getPlayer().inventory.currentItem != fireballSlot) {
                PacketUtil.send(new C09PacketHeldItemChange(fireballSlot));
            } else {
                RotationUtil.setClientRotation(new float[]{mc.getPlayer().rotationYaw + 180, 80f});
                PacketUtil.send(new C08PacketPlayerBlockPlacement(InventoryUtil.getSlot(mc.getPlayer().inventory.currentItem).getStack()));
                sent = true;
            }
        } else {
            if (!reset) {
                PacketUtil.send(new C09PacketHeldItemChange(mc.getPlayer().inventory.currentItem));
                reset = true;
            }
            if (gotVelo && mc.getPlayer().onGround) {
                Slack.getInstance().getModuleManager().getInstance(Flight.class).setToggle(false);
            }

            if (gotVelo && mc.getPlayer().hurtTime == 9) {
                MovementUtil.strafe(MovementUtil.getSpeed() * 1.1f);
            }
        }
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            if (((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.getPlayer().getEntityId())
                gotVelo = true;
        }
    }

    @Override
    public String toString() {
        return "Fireball Flight";
    }
}
