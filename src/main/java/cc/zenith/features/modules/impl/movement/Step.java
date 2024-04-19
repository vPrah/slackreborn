package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.utils.client.MC;
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
        if (MC.getPlayer().isCollidedHorizontally && MC.getPlayer().onGround) {
            switch (mode.getValue().toLowerCase()) {
                case "vanilla":
                    MC.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(MC.getPlayer().posX, MC.getPlayer().posY + 0.42f, MC.getPlayer().posZ, MC.getPlayer().onGround));
                    MC.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(MC.getPlayer().posX, MC.getPlayer().posY + 0.72f, MC.getPlayer().posZ, MC.getPlayer().onGround));
                    MC.getPlayer().stepHeight = 1f;
                    break;
                case "verus":
                    MC.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(MC.getPlayer().posX, MC.getPlayer().posY + 0.42f, MC.getPlayer().posZ, MC.getPlayer().onGround));
                    MC.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(MC.getPlayer().posX, MC.getPlayer().posY + 0.85f, MC.getPlayer().posZ, MC.getPlayer().onGround));
                    MC.getPlayer().stepHeight = 1f;
                    MC.getTimer().timerSpeed = 0.91f;
                    break;
            }
        } else {
            MC.getPlayer().stepHeight = 0.5f;
            MC.getTimer().timerSpeed = 1f;
        }
    }
}
