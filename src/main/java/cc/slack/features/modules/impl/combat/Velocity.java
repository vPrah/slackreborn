// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.combat;

import cc.slack.Slack;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.features.modules.impl.movement.Flight;
import cc.slack.features.modules.impl.movement.flights.impl.vanilla.FireballFlight;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.BlinkUtil;
import cc.slack.utils.player.MovementUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(
        name = "Velocity",
        category = Category.COMBAT
)

public class Velocity extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Cancel", "Motion", "Tick", "Reverse", "Hypixel", "Hypixel Damage Strafe", "Hypixel Test", "Intave"});

    private final NumberValue<Integer> vertical = new NumberValue<>("Vertical", 100, 0, 100, 1);
    private final NumberValue<Integer> horizontal = new NumberValue<>("Horizontal", 0, 0, 100, 1);
    private final NumberValue<Integer> velocityTick = new NumberValue<>("Velocity Tick", 5, 0, 20, 1);
    private final BooleanValue onlyground = new BooleanValue("Only Ground", false);
    private final BooleanValue noFire = new BooleanValue("No Fire", false);

    int jumped = 0;
    boolean hypixeltest= false;
    double hypixelY;

    public Velocity() {
        addSettings(mode, vertical, horizontal, velocityTick, onlyground, noFire);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (mc.getPlayer() == null || mc.getWorld() == null) return;
        if (noFire.getValue() && mc.getPlayer().isBurning()) return;

        if (onlyground.getValue() && !mc.getPlayer().onGround) {
            return;
        }

        if (Slack.getInstance().getModuleManager().getInstance(Flight.class).isToggle() && Slack.getInstance().getModuleManager().getInstance(Flight.class).mode.getValue() == new FireballFlight()) {
            return;
        }

        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = event.getPacket();
            if (packet.getEntityID() == mc.getPlayer().getEntityId()) {
                switch (mode.getValue().toLowerCase()) {
                    case "motion":
                        if (horizontal.getValue() == 0) {
                            event.cancel();
                            mc.getPlayer().motionY = packet.getMotionY() * vertical.getValue().doubleValue() / 100 / 8000.0;
                        } else if (vertical.getValue() == 0) {
                            event.cancel();
                            mc.getPlayer().motionX = packet.getMotionX() * horizontal.getValue().doubleValue() / 100 / 8000.0;
                            mc.getPlayer().motionZ = packet.getMotionZ() * horizontal.getValue().doubleValue() / 100 / 8000.0;
                        } else {
                            packet.setMotionX(packet.getMotionX() * (horizontal.getValue() / 100));
                            packet.setMotionY(packet.getMotionY() * (vertical.getValue() / 100));
                            packet.setMotionZ(packet.getMotionZ() * (horizontal.getValue() / 100));
                            event.setPacket(packet);
                        }
                        break;
                    case "cancel":
                        event.cancel();
                        break;
                    case "hypixel":
                        event.cancel();
                        mc.getPlayer().motionY = packet.getMotionY() * vertical.getValue().doubleValue() / 100 / 8000.0;
                        break;
                    case "hypixel test":
                        if (mc.getPlayer().onGround) {
                            if (hypixeltest) {
                                mc.getPlayer().motionY = hypixelY;
                                BlinkUtil.disable();
                                hypixeltest = false;
                            } else {
                                event.cancel();
                                mc.getPlayer().motionY = packet.getMotionY() * vertical.getValue().doubleValue() / 100 / 8000.0;
                                hypixeltest = false;
                            }
                        } else {
                            if (hypixeltest) {
                                event.cancel();
                                hypixelY = packet.getMotionY() * vertical.getValue().doubleValue() / 100 / 8000.0;
                            } else {
                                event.cancel();
                                hypixelY = packet.getMotionY() * vertical.getValue().doubleValue() / 100 / 8000.0;
                                BlinkUtil.enable(true, false);
                                hypixeltest = true;
                            }
                        }
                    case "reverse":
                        event.cancel();
                        mc.getPlayer().motionY = packet.getMotionY() / 8000.0;
                        mc.getPlayer().motionX = packet.getMotionX() / 8000.0;
                        mc.getPlayer().motionZ = packet.getMotionZ() / 8000.0;
                        MovementUtil.strafe();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().isInWater() || mc.getPlayer().isInLava() || mc.getPlayer().isInWeb) {
            return;
        }


        switch (mode.getValue().toLowerCase()) {
            case "intave":
                if (mc.getPlayer().hurtTime == 9) {
                    if (++jumped % 2 == 0 && mc.getPlayer().onGround && mc.getPlayer().isSprinting() && mc.getCurrentScreen() == null) {
                        mc.getGameSettings().keyBindJump.pressed = true;
                        jumped = 0;
                    }
                } else {
                    mc.getGameSettings().keyBindJump.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindJump);
                }
                break;
            case "hypixel damage strafe":
                if (mc.getPlayer().hurtTime == 9) {
                    MovementUtil.strafe(MovementUtil.getSpeed() * 0.8f);
                }
                break;
            case "hypixel":
                if (mc.getPlayer().hurtTime == 9) {
                    MovementUtil.strafe(MovementUtil.getSpeed() * 0.9f);
                }
                break;
            case "hypixel test":
                if (mc.getPlayer().onGround) {
                    if (hypixeltest) {
                        mc.getPlayer().motionY = hypixelY;
                        BlinkUtil.disable();
                        hypixeltest = false;
                    }
                }
            case "tick":
                if (mc.getPlayer().ticksSinceLastDamage == velocityTick.getValue()) {
                    mc.getPlayer().motionX *= horizontal.getValue().doubleValue() / 100;
                    mc.getPlayer().motionY *= vertical.getValue().doubleValue() / 100;
                    mc.getPlayer().motionZ *= horizontal.getValue().doubleValue() / 100;
                }
            default:
                break;
        }
    }

    @Override
    public String getMode() { return mode.getValue(); }

}