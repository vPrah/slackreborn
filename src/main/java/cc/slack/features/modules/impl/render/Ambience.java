// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;

@ModuleInfo(
        name = "Ambience",
        category = Category.RENDER
)
public class Ambience extends Module {

    public ModeValue<String> timemode = new ModeValue<>("Time", new String[]{"None","Sun", "Night", "Custom"});
    private final NumberValue<Integer> customtime = new NumberValue<>("Custom Time", 5, 1, 24, 1);
    public ModeValue<String> weathermode = new ModeValue<>("Weather", new String[]{"None", "Clear", "Rain", "Thunder"});
    private final NumberValue<Float> weatherstrength = new NumberValue<>("Weather Strength", 1F, 0F, 1F,  0.01F);



    public Ambience() {
        addSettings(timemode, customtime, weathermode, weatherstrength);
    }

    @Listen
    @SuppressWarnings("unused")
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

        switch (weathermode.getValue()) {
            case "Clear":
                mc.getWorld().setRainStrength(0F);
                mc.getWorld().setThunderStrength(0F);
                break;
            case "Rain":
                mc.getWorld().setRainStrength(weatherstrength.getValue());
                mc.getWorld().setThunderStrength(0F);
                break;
            case "Thunder":
                mc.getWorld().setRainStrength(weatherstrength.getValue());
                mc.getWorld().setThunderStrength(weatherstrength.getValue());
                break;
        }
    }

    @Listen
    public void onPacket (PacketEvent event) {
        if (!timemode.getValue().contains("None") && event.getPacket() instanceof S03PacketTimeUpdate) {
            event.cancel();
        }

        if (!weathermode.getValue().contains("None") && event.getPacket() instanceof S2BPacketChangeGameState) {
            if (((S2BPacketChangeGameState) event.getPacket()).getGameState() >= 7 && ((S2BPacketChangeGameState) event.getPacket()).getGameState() <= 8) {
                event.cancel();
            }
        }
    }

    @Override
    public String getMode() { return timemode.getValue(); }
}
