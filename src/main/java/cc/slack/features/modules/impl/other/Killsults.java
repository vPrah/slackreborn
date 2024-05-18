package cc.slack.features.modules.impl.other;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.concurrent.ThreadLocalRandom;


@ModuleInfo(
        name = "Killsults",
        category = Category.OTHER
)
public class Killsults extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Universocraft"});


    public Killsults() {
        addSettings(mode);
    }

    @Listen
    public void onPacket (PacketEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            final S02PacketChat packet = event.getPacket();
            final String message = packet.getChatComponent().getUnformattedText();
            switch (mode.getValue()){
                case "Universocraft":
                    if (message.contains(mc.getPlayer().getNameClear()) && message.contains("fue brutalmente asesinado por") || message.contains(mc.getPlayer().getNameClear()) && message.contains("fue empujado al vacío por") || message.contains(mc.getPlayer().getNameClear()) && message.contains("no resistió los ataques de") || message.contains(mc.getPlayer().getNameClear()) && message.contains("pensó que era un buen momento de morir a manos de") || message.contains(mc.getPlayer().getNameClear()) && message.contains("ha sido asesinado por")) {
                        sendInsult();
                        break;
                    }
                    break;
                case "":
                break;
            }
        }
    }

    public void sendInsult() {
        final String[] insultslist = { "U r a piece of yyshit", "Slack > All minecraft clients", "Do you know how to use your hands?", "DG chews me", "Bro, he is using FDP" };
        final int randomIndex = ThreadLocalRandom.current().nextInt(0, insultslist.length);
        mc.getPlayer().sendChatMessage(insultslist[randomIndex]);
    }
}
