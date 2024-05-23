package cc.slack.features.modules.impl.other;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.slack.Slack;
import cc.slack.events.impl.game.TickEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.utils.client.mc;
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
    private final AtomicBoolean started = new AtomicBoolean(false);
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final StringBuilder str2 = new StringBuilder();

    public RichPresence() {
        addSettings(showNickname);
    }

    @Override
    public void onDisable() {
        DiscordRPC.discordShutdown();
        started.set(false);
    }

    @Listen
    public void onTick(TickEvent event) {
        if (showNickname.getValue()) {
            String playerName = mc.getPlayer() != null ? mc.getPlayer().getNameClear() : mc.getMinecraft().session.getUsername();
            str2.setLength(0);
            str2.append("IGN: ").append(playerName).append("\n");
        }
        
        startDiscordThread();
    }

    private void startDiscordThread() {
        if (started.compareAndSet(false, true)) {
        	scheduler.execute(() -> {
                if (!isToggle()) {
                    return;
                }

               final DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(str2.toString())
                    .setDetails("Playing Slack Client")
                    .setBigImage("slack", "Slack Client " + Slack.getInstance().info.getVersion())
                    .setStartTimestamps(System.currentTimeMillis());

                DiscordRPC.discordUpdatePresence(builder.build());
                DiscordRPC.discordRunCallbacks();

        	});

            DiscordEventHandlers handlers = new DiscordEventHandlers();
            DiscordRPC.discordInitialize("1241556030664736788", handlers, true);
        }
    }

}
