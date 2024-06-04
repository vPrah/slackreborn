// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.utilties;

import cc.slack.Slack;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.impl.render.HUD;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;

@ModuleInfo(
        name = "AutoPlay",
        category = Category.UTILITIES
)
public class AutoPlay extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Universocraft", "Librecraft", "Hypixel"});
    private final ModeValue<String> univalue = new ModeValue<>("Universocraft", new String[]{"Skywars", "Bedwars", "Eggwars", "Hungergames"});


    public AutoPlay() {
        super();
        addSettings(mode, univalue);
    }

    private void process(IChatComponent chatComponent) {
        String value = chatComponent.getChatStyle().getChatClickEvent().getValue();
        if (value != null && value.startsWith("/play") && !value.startsWith("/play skyblock")) {
            mc.getPlayer().sendChatMessage(value);
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
            case "Universocraft":
                if (unformattedText.contains("Jugar de nuevo") || unformattedText.contains("Ha ganado")) {
                    switch (univalue.getValue()) {
                        case "Skywars":
                            mc.getPlayer().sendChatMessage("/skywars random");
                            break;
                        case "Bedwars":
                            mc.getPlayer().sendChatMessage("/bedwars random");
                            break;
                        case "Eggwars":
                            mc.getPlayer().sendChatMessage("/eggwars random");
                            break;
                        case "Hungergames":
                            mc.getPlayer().sendChatMessage("/playagain");
                            break;
                    }
                    iscorrectjoin();
                }
                break;
            case "Librecraft":
                if (unformattedText.contains("¡Partida finalizada!")) {
                    mc.getPlayer().sendChatMessage("/saliryentrar");
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
