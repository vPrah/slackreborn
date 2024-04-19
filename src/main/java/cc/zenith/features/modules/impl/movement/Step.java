package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
        name = "Step",
        category = Category.MOVEMENT
)
public class Step extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Vanilla", "Verus"});


    public Step() {
        addSettings(mode);
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().isCollidedHorizontally && mc.getPlayer().onGround) {
            switch (mode.getValue().toLowerCase()) {
                case "vanilla":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY + 0.42f, mc.getPlayer().posZ, mc.getPlayer().onGround));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY + 0.72f, mc.getPlayer().posZ, mc.getPlayer().onGround));
                    mc.getPlayer().stepHeight = 1f;
                    break;
                case "verus":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY + 0.42f, mc.getPlayer().posZ, mc.getPlayer().onGround));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY + 0.85f, mc.getPlayer().posZ, mc.getPlayer().onGround));
                    mc.getPlayer().stepHeight = 1f;
                    mc.getTimer().timerSpeed = 0.91f;
                    break;
            }
        } else {
            mc.getPlayer().stepHeight = 0.5f;
            mc.getTimer().timerSpeed = 1f;
        }
    }
}
