// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
        name = "Step",
        category = Category.MOVEMENT
)
public class Step extends Module {

    private final ModeValue<String> mode = new ModeValue<>("Step Mode", new String[]{"Vanilla", "NCP", "Verus", "Vulcan"});
    private final NumberValue<Float> timerSpeed = new NumberValue<>("Timer", 1f, 0f, 2f, 0.05f);


    public Step() {
        addSettings(mode, timerSpeed);
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().isCollidedHorizontally && mc.getPlayer().onGround) {
            mc.getTimer().timerSpeed = timerSpeed.getValue();
            switch (mode.getValue().toLowerCase()) {
                case "vanilla":
                    mc.getPlayer().stepHeight = 1f;
                    break;
                case "verus":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY + 0.42f, mc.getPlayer().posZ, mc.getPlayer().onGround));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY + 0.85f, mc.getPlayer().posZ, mc.getPlayer().onGround));
                    mc.getPlayer().stepHeight = 1f;
                    mc.getTimer().timerSpeed = 0.91f;
                    break;
                case "ncp":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY + 0.42f, mc.getPlayer().posZ, mc.getPlayer().onGround));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY + 0.7532f, mc.getPlayer().posZ, mc.getPlayer().onGround));
                    mc.getPlayer().stepHeight = 1f;
                    break;
                case "vulcan":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY + 0.5f, mc.getPlayer().posZ, mc.getPlayer().onGround));
                    mc.getPlayer().stepHeight = 1f;
                    break;

            }
        } else {
            mc.getPlayer().stepHeight = 0.5f;
            mc.getTimer().timerSpeed = 1f;
        }
    }
}
