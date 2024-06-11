package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
        name = "VClip",
        category = Category.MOVEMENT
)
public class VClip extends Module {

    private final NumberValue<Double> clipValue = new NumberValue<>("Down Value", 4.0D, 0.5D, 10D, 0.5D);


    public VClip() {
        addSettings(clipValue);
    }

    @Listen
    public void onUpdate (UpdateEvent event) {
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY - clipValue.getValue(), mc.getPlayer().posZ, true));
        mc.getPlayer().setPosition(mc.getPlayer().posX, mc.getPlayer().posY - clipValue.getValue(), mc.getPlayer().posZ);
    }
}
