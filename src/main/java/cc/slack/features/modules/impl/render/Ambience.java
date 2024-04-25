package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@ModuleInfo(
        name = "Aimbience",
        category = Category.RENDER
)
public class Ambience extends Module {

    public ModeValue<String> timemode = new ModeValue<>("Time Mode", new String[]{"Sun", "Night", "Custom"});
    private final NumberValue<Integer> customtime = new NumberValue<>("Custom Time", 5, 1, 24, 1);


    public Ambience() {
        addSettings(timemode, customtime);
    }

    @Listen
    public void onUpdate (UpdateEvent event) {
        switch (timemode.getValue()) {
            case "Sun":
                mc.getWorld().setWorldTime(6000);
                break;
            case "Night":
                mc.getWorld().setWorldTime(15000);
                break;
            case "Custom":
                mc.getWorld().setWorldTime(customtime.getValue() * 1000);
                break;
        }
    }

    @Listen
    public void onPacket (PacketEvent event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            event.cancel();
        }
    }
}
