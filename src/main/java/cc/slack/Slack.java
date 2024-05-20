package cc.slack;

import cc.slack.events.Event;
import cc.slack.events.impl.game.ChatEvent;
import cc.slack.events.impl.input.KeyEvent;
import cc.slack.features.commands.CMDManager;
import cc.slack.features.commands.api.CMD;
import cc.slack.features.config.configManager;
import cc.slack.features.modules.ModuleManager;
import cc.slack.features.modules.impl.movement.Sprint;
import cc.slack.features.modules.impl.other.RichPresence;
import cc.slack.features.modules.impl.other.Targets;
import cc.slack.features.modules.impl.other.Tweaks;
import cc.slack.features.modules.impl.render.Animations;
import cc.slack.features.modules.impl.render.HUD;
import cc.slack.features.modules.impl.render.ScoreboardModule;
import cc.slack.features.modules.impl.render.TargetHUD;
import cc.slack.utils.client.ClientInfo;
import cc.slack.utils.EventUtil;
import cc.slack.utils.other.PrintUtil;
import de.florianmichael.viamcp.ViaMCP;
import io.github.nevalackin.radbus.Listen;
import io.github.nevalackin.radbus.PubSub;
import lombok.Getter;
import org.lwjgl.opengl.Display;

import java.util.Arrays;
import java.util.EnumMap;

@Getter
@SuppressWarnings("unused")
public class Slack {

    @Getter
    private static final Slack instance = new Slack();
    public final ClientInfo info = new ClientInfo("Slack", "v0.1", ClientInfo.VersionType.ALPHA);
    private final PubSub<Event> eventBus = PubSub.newInstance(System.err::println);

    private final ModuleManager moduleManager = new ModuleManager();
    private final CMDManager cmdManager = new CMDManager();

    public final String changelog = "Release v0.01:" +
            "\r\n-Added all modules (56)" +
            "\r\n-Added SexModule";

    public void start() {
        PrintUtil.print("Initializing " + info.getName());
        Display.setTitle(info.getName() + " " + info.getVersion() + " | " + info.getType() + " Build");

        EventUtil.register(this);
        moduleManager.initialize();
        cmdManager.initialize();
        configManager.init();


        // Default Modules
        moduleManager.getInstance(Animations.class).toggle();
        moduleManager.getInstance(HUD.class).toggle();
        moduleManager.getInstance(Sprint.class).toggle();
        moduleManager.getInstance(Tweaks.class).toggle();
        moduleManager.getInstance(TargetHUD.class).toggle();
        moduleManager.getInstance(Targets.class).toggle();


        try {
            ViaMCP.create();
            ViaMCP.INSTANCE.initAsyncSlider();
        } catch (Exception e) {
            // Dont more StrackTrace
        }
    }

    public void shutdown() {
        EventUtil.unRegister(this);
        configManager.stop();
    }

    // Event Stuff

    @Listen
    public void handleKey(KeyEvent e) {
        moduleManager.getModules().forEach(module -> {
            if (module.getKey() == e.getKey())
                module.toggle();
        });
    }

    @Listen
    public void handleChat(ChatEvent e) {
        String message = e.getMessage();

        if (!message.startsWith(cmdManager.getPrefix())) return;
        e.cancel();

        message = message.substring(cmdManager.getPrefix().length());

        if (message.split("\\s")[0].equalsIgnoreCase("")) {
            PrintUtil.message("This is §cSlack's§f prefix for ingame client commands. Type §c.help §fto get started.");
            return;
        }

        if (message.split("\\s").length > 0) {
            final String cmdName = message.split("\\s")[0];

            for (CMD cmd : cmdManager.getCommands()) {
                if (cmd.getName().equalsIgnoreCase(cmdName) || cmd.getAlias().equalsIgnoreCase(cmdName)) {
                    cmd.onCommand(Arrays.copyOfRange(message.split("\\s"), 1, message.split("\\s").length), message);
                    return;
                }
            }

            PrintUtil.message("\"" + message + "\" is not a recognized command. Use §c.help §fto get other commands.");
        }
    }

    public enum NotificationStyle {
        GRAY, SUCCESS, FAIL, WARN
    }

    public void addNotification(String bigText, String smallText, Long duration, NotificationStyle style) {
        instance.getModuleManager().getInstance(HUD.class).addNotification(bigText, smallText, duration, style);
    }
}
