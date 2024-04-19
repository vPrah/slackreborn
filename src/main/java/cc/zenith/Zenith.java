package cc.zenith;

import cc.zenith.events.Event;
import cc.zenith.events.impl.game.ChatEvent;
import cc.zenith.events.impl.input.KeyEvent;
import cc.zenith.features.commands.CMDManager;
import cc.zenith.features.commands.api.CMD;
import cc.zenith.features.modules.ModuleManager;
import cc.zenith.features.modules.impl.movement.Sprint;
import cc.zenith.features.modules.impl.render.HUD;
import cc.zenith.utils.client.ClientInfo;
import cc.zenith.utils.EventUtil;
import cc.zenith.utils.other.PrintUtil;
import de.florianmichael.viamcp.ViaMCP;
import io.github.nevalackin.radbus.Listen;
import io.github.nevalackin.radbus.PubSub;
import lombok.Getter;
import org.lwjgl.opengl.Display;

import java.util.Arrays;

@Getter
@SuppressWarnings("unused")
public class Zenith {

    @Getter
    private static final Zenith instance = new Zenith();
    private final ClientInfo info = new ClientInfo("Zenith", "v0.0", ClientInfo.VersionType.ALPHA);
    private final PubSub<Event> eventBus = PubSub.newInstance(System.err::println);

    private final ModuleManager moduleManager = new ModuleManager();
    private final CMDManager cmdManager = new CMDManager();

    public void start() {
        PrintUtil.print("Initializing " + info.getName());
        Display.setTitle(info.getName() + " " + info.getVersion() + " | " + info.getType() + " Build");

        EventUtil.register(this);
        moduleManager.initialize();
        cmdManager.initialize();

        moduleManager.getInstance(HUD.class).toggle();
        moduleManager.getInstance(Sprint.class).toggle();

        try {
            ViaMCP.create();
            ViaMCP.INSTANCE.initAsyncSlider();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        EventUtil.unRegister(this);
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
            PrintUtil.message("This is §cZenith's§f prefix for ingame client commands. Type §c.help §fto get started.");
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
}
