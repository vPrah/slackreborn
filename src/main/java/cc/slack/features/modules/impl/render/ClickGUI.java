// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.ui.NewCGUI.TransparentClickGUI;
import cc.slack.ui.clickGUI.ClickGui;
import cc.slack.utils.client.mc;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "ClickGUI",
        category = Category.RENDER,
        key = Keyboard.KEY_RSHIFT
)

public class ClickGUI<ClickGUIType extends GuiScreen> extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Old", "New"});
    private ClickGUIType clickgui;
    //private boolean isOpen;

    public ClickGUI() {
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        if (clickgui == null) {
            switch (mode.getValue()) {
                case "New":
//                    clickgui = (ClickGUIType) new TransparentClickGUI();
                    break;
                case "Old":
                    clickgui = (ClickGUIType) new ClickGui();
                    break;
                default:
                    throw new RuntimeException("Unknown Type: ClickGUI");
            }
        }

        //isOpen = !isOpen;

        mc.getMinecraft().displayGuiScreen(/*isOpen ? null : */clickgui);
        toggle();
    }


}