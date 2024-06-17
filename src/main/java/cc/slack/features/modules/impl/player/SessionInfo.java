package cc.slack.features.modules.impl.player;

import cc.slack.Slack;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.features.modules.impl.utilties.NameProtect;
import cc.slack.utils.font.Fonts;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S40PacketDisconnect;

import java.awt.*;

@ModuleInfo(
        name = "SessionInfo",
        category = Category.RENDER
)
public class SessionInfo extends Module {

    private final NumberValue<Float> xValue = new NumberValue<>("Pos X", 8.0F, 1.0F, 300.0F, 1F);
    private final NumberValue<Float> yValue = new NumberValue<>("Pos Y", 160F, 1.0F, 300.0F, 1F);
    private final NumberValue<Integer> alphaValue = new NumberValue<>("Alpha", 170, 0, 255, 1);
    private final BooleanValue roundedValue = new BooleanValue("Rounded", false);


    public long gameJoined;
    public long killAmount;
    public long currentTime;
    public long timeJoined;

    public SessionInfo() {
        addSettings(xValue,yValue,alphaValue,roundedValue);
    }


    @Override
    public void onEnable() {
        currentTime = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        currentTime = 0L;
        timeJoined = System.currentTimeMillis();
    }

    @Listen
    public void onPacket (PacketEvent event) {
        try {
            if (event.getPacket() instanceof S02PacketChat) {
                S02PacketChat packet = event.getPacket();
                String message = packet.getChatComponent().getUnformattedText();
                if (message.contains(mc.getSession().getUsername() + " wants to fight!")) {
                    ++gameJoined;
                }
                if (message.contains(mc.getSession().getUsername() + " has joined")) {
                    ++gameJoined;
                }
                if (message.contains("by " + mc.getSession().getUsername())) {
                    ++killAmount;
                }
            }
            if (event.getPacket() instanceof S40PacketDisconnect) {
                currentTime = 0L;
                timeJoined = System.currentTimeMillis();
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    public String getSessionLengthString() {
        long totalSeconds = (System.currentTimeMillis() - timeJoined) / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = totalSeconds % 3600L / 60L;
        long seconds = totalSeconds % 60L;
        return (hours > 0L ? hours + "h, " : "") + (minutes > 0L ? minutes + "m, " : "") + seconds + "s";
    }

    @SuppressWarnings("unused")
    @Listen
    public void onRender (RenderEvent event) {
        if (mc.gameSettings.showDebugInfo) {
            return;
        }
        int x = xValue.getValue().intValue();
        int y = yValue.getValue().intValue();
        String username = Slack.getInstance().getModuleManager().getInstance(NameProtect.class).isToggle() ? "Slack User" : mc.getSession().getUsername();
        if (roundedValue.getValue()) {
            RenderUtil.drawRoundedRect(x, y, x + 170, y + 58, 8.0, new Color(0,0,0,alphaValue.getValue()).getRGB());
        } else {
            RenderUtil.drawRoundedRect(x, y, x + 170, y + 58, 1.0, new Color(0,0,0,alphaValue.getValue()).getRGB());
        }
        Fonts.apple20.drawStringWithShadow("Session Info", x + 53, y + 8, -1);
        Fonts.apple20.drawStringWithShadow("Play time: " + getSessionLengthString(), x + 8, y + 22, -1);
        Fonts.apple20.drawStringWithShadow("Ign: " + username, x + 8, y + 34, -1);
        Fonts.apple20.drawStringWithShadow("Kills: " + killAmount, x + 8, y + 46, -1);
    }


}
