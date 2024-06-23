package cc.slack.features.modules.impl.utilties;
import cc.slack.features.modules.api.settings.impl.StringValue;

import cc.slack.events.impl.game.TickEvent;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.other.TimeUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import org.apache.commons.lang3.StringUtils;



@ModuleInfo(
        name = "AutoLogin",
        category = Category.UTILITIES
)
public class AutoLogin extends Module {

    private final StringValue password = new StringValue("Password", "Aethermariconkj23");

    private String text;
    private final TimeUtil timeUtil;

    public AutoLogin() {
        timeUtil = new TimeUtil();
    }

    @Listen
    public void onPacket (PacketEvent event) {
        final Packet packet = event.getPacket();
        if (packet instanceof S02PacketChat) {
            final S02PacketChat s02PacketChat = (S02PacketChat)packet;
            String text = s02PacketChat.getChatComponent().getUnformattedText();
            if (StringUtils.containsIgnoreCase(text, "/register") || StringUtils.containsIgnoreCase(text, "/register password password") || text.equalsIgnoreCase("/register <password> <password>")) {
                this.text = "/register "  + password.getValue() + " "  + password.getValue();
                timeUtil.reset();
            }
            else if (StringUtils.containsIgnoreCase(text, "/login password") || StringUtils.containsIgnoreCase(text, "/login") || text.equalsIgnoreCase("/login <password>")) {
                this.text = "/login " + password.getValue();
                timeUtil.reset();
            }
        }
    }

    @SuppressWarnings("unused")
    @Listen
    public void onTick (TickEvent event) {
        if (timeUtil.hasReached(1500L) && text != null && !text.isEmpty()) {
            mc.thePlayer.sendChatMessage(text);
            System.out.println(text);
            text = "";
        }
    }

}
