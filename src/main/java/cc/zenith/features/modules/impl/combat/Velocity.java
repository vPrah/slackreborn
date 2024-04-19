package cc.zenith.features.modules.impl.combat;

import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.BooleanValue;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.features.modules.api.settings.impl.NumberValue;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.player.MovementUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(
        name = "Velocity",
        category = Category.COMBAT
)

public class Velocity extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Vanilla", "Hypixel", "Cancel", "Intave"});

    private final NumberValue<Integer> vertical = new NumberValue<>("Vertical", 100, 0, 100, 1);
    private final NumberValue<Integer> horizontal = new NumberValue<>("Horizontal", 0, 0, 100, 1);

    private final BooleanValue noFire = new BooleanValue("NoFire", false);

    int jumped = 0;

    public Velocity() {
        super();
        addSettings(mode, vertical, horizontal, noFire);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (mc.getPlayer() == null || mc.getWorld() == null) return;
        if (event.getDirection() != PacketDirection.OUTGOING) return;
        if (noFire.getValue() && mc.getPlayer().isBurning()) return;

        if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.getPlayer().getEntityId()) {
            S12PacketEntityVelocity packet = event.getPacket();
            switch (mode.getValue().toLowerCase()) {
                case "vanilla":
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
                    }
                    break;
                case "cancel":
                    event.cancel();
                    break;
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
                case "hypixel":
                    event.cancel();
                    mc.getPlayer().motionY = packet.getMotionY() * vertical.getValue().doubleValue() / 100 / 8000.0;
                    break;
                case "reverse":
                    event.cancel();
                    mc.getPlayer().motionY = packet.getMotionY() / 8000.0;
                    mc.getPlayer().motionX = packet.getMotionX() / 8000.0;
                    mc.getPlayer().motionZ = packet.getMotionZ() / 8000.0;
                    MovementUtil.strafe();
            }
        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mode.getValue().contains("Intave")) {
            if (mc.getCurrentScreen() != null) {
            }
        }
    }

}