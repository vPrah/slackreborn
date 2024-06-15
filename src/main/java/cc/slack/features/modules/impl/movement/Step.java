// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
        name = "Step",
        category = Category.MOVEMENT
)
public class Step extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Vanilla", "NCP", "Verus", "Vulcan"});
    private final NumberValue<Float> timerSpeed = new NumberValue<>("Timer", 1f, 0f, 2f, 0.05f);


    public Step() {
        addSettings(mode, timerSpeed);
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround) {
            mc.timer.timerSpeed = timerSpeed.getValue();
            switch (mode.getValue().toLowerCase()) {
                case "vanilla":
                    mc.thePlayer.stepHeight = 1f;
                    break;
                case "verus":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42f, mc.thePlayer.posZ, mc.thePlayer.onGround));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.85f, mc.thePlayer.posZ, mc.thePlayer.onGround));
                    mc.thePlayer.stepHeight = 1f;
                    mc.timer.timerSpeed = 0.91f;
                    break;
                case "ncp":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42f, mc.thePlayer.posZ, mc.thePlayer.onGround));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.7532f, mc.thePlayer.posZ, mc.thePlayer.onGround));
                    mc.thePlayer.stepHeight = 1f;
                    break;
                case "vulcan":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5f, mc.thePlayer.posZ, mc.thePlayer.onGround));
                    mc.thePlayer.stepHeight = 1f;
                    break;

            }
        } else {
            mc.thePlayer.stepHeight = 0.5f;
            mc.timer.timerSpeed = 1f;
        }
    }
    
}
