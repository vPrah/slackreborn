// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.utilties;

import cc.slack.Slack;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.impl.render.HUD;
import cc.slack.utils.other.TimeUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;

@ModuleInfo(
        name = "AutoPlay",
        category = Category.UTILITIES
)
public class AutoPlay extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Universocraft", "Zonecraft" ,"Librecraft", "Hypixel"});


    private final TimeUtil timeUtil;

    public AutoPlay() {
        super();
        timeUtil = new TimeUtil();
        addSettings(mode);
    }

    private void process(IChatComponent chatComponent) {
        String value = chatComponent.getChatStyle().getChatClickEvent().getValue();
        if (value != null && value.startsWith("/play") && !value.startsWith("/play skyblock")) {
            mc.thePlayer.sendChatMessage(value);
        } else {
            for (IChatComponent component : chatComponent.getSiblings()) {
                process(component);
            }
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (!(event.getPacket() instanceof S02PacketChat)) return;

        IChatComponent chatComponent = ((S02PacketChat) event.getPacket()).getChatComponent();
        String unformattedText = chatComponent.getUnformattedText();

        switch (mode.getValue()) {
            case "Hypixel":
                process(chatComponent);
                break;
            case "Zonecraft":
            case "Universocraft":
                String text = ((S02PacketChat) event.getPacket()).getChatComponent().getUnformattedText();
                if (!text.toLowerCase().contains("jugar de nuevo"))
                    return;

                IChatComponent commandTextComponent = null;
                for (IChatComponent sibling : ((S02PacketChat) event.getPacket()).getChatComponent().getSiblings()) {
                    if (sibling.getUnformattedText().toLowerCase().contains("jugar de nuevo")) {
                        commandTextComponent = sibling;
                        break;
                    }
                }

                if (commandTextComponent == null)
                    return;

                String command = commandTextComponent.getChatStyle().getChatClickEvent().getValue();
                if (timeUtil.hasReached(500L)) {
                    mc.thePlayer.sendChatMessage(command);
                    iscorrectjoin();
                    timeUtil.reset();
                }
                break;
            case "Librecraft":
                if (unformattedText.contains("¡Partida finalizada!")) {
                    mc.thePlayer.sendChatMessage("/saliryentrar");
                    iscorrectjoin();
                }
                break;
        }
    }

    public void iscorrectjoin() {
        Slack.getInstance().getModuleManager().getInstance(HUD.class).addNotification("AutoPlay:  You joined in the new game", "", 1500L, Slack.NotificationStyle.WARN);
    }

    @Override
    public String getMode() { return mode.getValue(); }

}
