package cc.slack.features.modules.impl.other;

import cc.slack.Slack;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.impl.render.HUD;
import cc.slack.utils.other.PrintUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;

@ModuleInfo(
        name = "Lightning Detector",
        category = Category.OTHER
)
public class LightningDetector extends Module {

    @Listen
    public void onPacket (PacketEvent event) {
        if (event.getDirection() == PacketDirection.INCOMING) {
            if (event.getPacket() instanceof S2CPacketSpawnGlobalEntity) {
                S2CPacketSpawnGlobalEntity S2C = event.getPacket();
                if (S2C.func_149053_g() == 1) {
                    double d0 = (double)S2C.func_149051_d() / 32.0D;
                    double d1 = (double)S2C.func_149050_e() / 32.0D;
                    double d2 = (double)S2C.func_149049_f() / 32.0D;
                    PrintUtil.message("Detected lightning strike at " + d0 + ", " + d1 + ", " + d2);
                    Slack.getInstance().getModuleManager().getInstance(HUD.class).addNotification("Lightning Detector:  Detected lightning strike at " + d0 + ", " + d1 + ", " + d2, "", 4500L, Slack.NotificationStyle.WARN);
                }
            }
        }
    }

}
