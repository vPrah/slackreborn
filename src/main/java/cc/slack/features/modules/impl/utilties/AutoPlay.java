// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.utilties;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;

@ModuleInfo(
        name = "AutoPlay",
        category = Category.UTILTIES
)
public class AutoPlay extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Hypixel", "Universocraft", "Librecraft"});
    private final ModeValue<String> univalue = new ModeValue<>("Universocraft", new String[]{"Skywars", "Bedwars"});


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
        if (event.getPacket() instanceof S02PacketChat) {
            switch (mode.getValue()) {
                case "Hypixel":
                    process(((S02PacketChat) event.getPacket()).getChatComponent());
                    break;
                case "Universocraft":
                    if (((S02PacketChat) event.getPacket()).getChatComponent().getUnformattedText().contains("Jugar de nuevo")) {
                        switch (univalue.getValue()) {
                            case "Skywars":
                                mc.getPlayer().sendChatMessage("/skywars random");
                                break;
                            case "Bedwars":
                                mc.getPlayer().sendChatMessage("/bedwars random");
                                break;
                        }
                    }
                    break;
                case "Librecraft":
                    if (((S02PacketChat) event.getPacket()).getChatComponent().getUnformattedText().contains("¡Partida finalizada!")) {
                        mc.getPlayer().sendChatMessage("/saliryentrar");
                    }
                    break;
            }
        }
    }
}
