// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.player;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(
        name = "AutoPlay",
        category = Category.PLAYER
)
public class AutoPlay extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Universocraft", "Librecraft"});
    private final ModeValue<String> univalue = new ModeValue<>("Universocraft", new String[]{"Skywars", "Bedwars"});


    public AutoPlay() {
        super();
        addSettings(mode, univalue);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            switch (mode.getValue()) {
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
