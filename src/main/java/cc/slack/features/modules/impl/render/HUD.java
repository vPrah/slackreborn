package cc.slack.features.modules.impl.render;

import cc.slack.Slack;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.features.modules.impl.render.hud.arraylist.impl.*;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;


import static net.minecraft.client.gui.Gui.drawRect;

@ModuleInfo(
        name = "HUD",
        category = Category.RENDER,
        key = Keyboard.KEY_M
)

public class HUD extends Module {

    private final BooleanValue watermarkvalue = new BooleanValue("WaterMark", true);
    private final ModeValue<IArraylist> arraylistModes = new ModeValue<>("Arraylist", new IArraylist[]{new BasicArrayList(), new Basic2ArrayList()});

    public HUD() {
        super();
        addSettings(arraylistModes, watermarkvalue);
    }

    @Listen
    @SuppressWarnings("unused")
    public void onRender(RenderEvent e) {
        if (e.state != RenderEvent.State.RENDER_2D) return;
            if (watermarkvalue.getValue()) {
                drawRect(2, 2, 85, 14, 0x80000000);
                Minecraft.getMinecraft().MCfontRenderer.drawString("Slack " + Slack.getInstance().getInfo().getVersion(), 4, 4, 0x5499C7);
                Minecraft.getMinecraft().MCfontRenderer.drawString(" - " + Minecraft.getDebugFPS(), 53, 4, -1);
            }
//        Render2DUtil.drawImage(new ResourceLocation("slack/textures/logo/trans-512.png"), 12, 12, 32, 32, new Color(255, 255, 255, 150));
        arraylistModes.getValue().onRender(e);
    }
}
