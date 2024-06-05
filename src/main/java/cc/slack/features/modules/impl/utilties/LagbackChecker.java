package cc.slack.features.modules.impl.utilties;

import cc.slack.Slack;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.impl.movement.Flight;
import cc.slack.features.modules.impl.movement.LongJump;
import cc.slack.features.modules.impl.movement.Speed;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.TimeUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleInfo(
        name = "LagbackChecker",
        category = Category.UTILITIES
)
public class LagbackChecker extends Module {

    private final BooleanValue disableValue = new BooleanValue("Disable Modules", true);

    private final TimeUtil timer = new TimeUtil();

    public LagbackChecker() {
        addSettings(disableValue);
    }

    @Listen
    public void onPacket (PacketEvent event) {
        if(event.getPacket() instanceof S08PacketPlayerPosLook && mc.getPlayer() != null && mc.getWorld() != null) {
            S08PacketPlayerPosLook packet = event.getPacket();

            double diffX  = mc.getPlayer().posX - packet.getX();
            double diffZ = mc.getPlayer().posZ - packet.getZ();
            Module[] modules = {Slack.getInstance().getModuleManager().getInstance(Flight.class), Slack.getInstance().getModuleManager().getInstance(Speed.class), Slack.getInstance().getModuleManager().getInstance(LongJump.class)};
            boolean toggled = false;

            for (Module module : modules) {
                if(module.isToggle()) {
                    toggled = true;
                    break;
                }
            }

            if(diffX + diffZ < 10 && timer.hasReached(1500) && toggled) {
                if (disableValue.getValue()) {
                    Slack.getInstance().addNotification("Lagback Detected! disabled all movements modules", "", 1500L, Slack.NotificationStyle.WARN);

                    for (Module module : modules) {
                        module.disableModule();
                    }
                } else {
                    Slack.getInstance().addNotification("Lagback Detected!", "", 1500L, Slack.NotificationStyle.WARN);
                }
                timer.reset();
            }
        }
    }

}
