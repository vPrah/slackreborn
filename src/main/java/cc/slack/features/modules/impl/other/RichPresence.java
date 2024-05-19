package cc.slack.features.modules.impl.other;

import cc.slack.Slack;
import cc.slack.events.impl.game.TickEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.PlayerUtil;
import io.github.nevalackin.radbus.Listen;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

@ModuleInfo(
        name = "DiscordRPC",
        category = Category.OTHER
)
public class RichPresence extends Module {

    private final BooleanValue showNickname = new BooleanValue("Show Nickname", true);

    private boolean started;

    String str2 = "";

    public RichPresence() {
       addSettings(showNickname);
    }



    @Listen
    public void onTick (TickEvent event) {

        if (showNickname.getValue()) {
            String playerName = mc.getPlayer() != null ? mc.getPlayer().getNameClear() : mc.getMinecraft().session.getUsername();
            str2 += "IGN: " + playerName + "\n";
        }

        if (!started) {
            final DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(str2) {{
                setDetails("Playing Slack Client");
                setBigImage("slack", "Slack Client " + Slack.getInstance().info.getVersion());
                setStartTimestamps(System.currentTimeMillis());
            }};

            DiscordRPC.discordUpdatePresence(builder.build());

            final DiscordEventHandlers handlers = new DiscordEventHandlers();
            DiscordRPC.discordInitialize("1241556030664736788", handlers, true);

            new Thread(() -> {
                while (isToggle()) {
                    DiscordRPC.discordRunCallbacks();
                }
            }, "Discord RPC Callback").start();

            started = true;
        }
    };

    @Override
    public void onDisable() {
        DiscordRPC.discordShutdown();
        started = false;
    }

}
