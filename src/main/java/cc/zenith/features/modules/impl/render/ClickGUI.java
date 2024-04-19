package cc.zenith.features.modules.impl.render;

import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.ui.NewCGUI.TransparentClickGUI;
import cc.zenith.ui.clickGUI.ClickGui;
import cc.zenith.utils.client.MC;
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

    @Override
    public void onEnable() {
        if (clickgui == null) {
            switch (mode.getValue().toLowerCase()) {
                case "new":
                    clickgui = (ClickGUIType) new TransparentClickGUI();
                    break;
                case "old":
                    clickgui = (ClickGUIType) new ClickGui();
                    break;
                default:
                    throw new RuntimeException("Unknown Type: ClickGUI");
            }
        }

        //isOpen = !isOpen;

        MC.getMinecraft().displayGuiScreen(/*isOpen ? null : */clickgui);
        toggle();
    }
}