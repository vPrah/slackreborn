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
import cc.slack.utils.client.mc;
import cc.slack.utils.font.Fonts;
import com.sun.org.apache.xpath.internal.operations.Mod;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;


import static net.minecraft.client.gui.Gui.drawRect;

@ModuleInfo(
        name = "HUD",
        category = Category.RENDER
)

public class HUD extends Module {
    private final ModeValue<IArraylist> arraylistModes = new ModeValue<>("Arraylist", new IArraylist[]{new BasicArrayList(), new Basic2ArrayList()});

    private final ModeValue<String> watermarksmodes = new ModeValue<>("WaterMark-Modes", new String[]{"Classic", "Backgrounded"});

    private final BooleanValue fpsdraw = new BooleanValue("ShowFPS", true);
    private final BooleanValue bpsdraw = new BooleanValue("ShowBPS", true);

    private final BooleanValue backgroundedraw = new BooleanValue("ShowBackgrounded", true);


    public HUD() {
        super();
        addSettings(arraylistModes, watermarksmodes, fpsdraw, bpsdraw);
    }

    @Listen
    @SuppressWarnings("unused")
    public void onRender(RenderEvent e) {
        if (e.state != RenderEvent.State.RENDER_2D) return;

            switch (watermarksmodes.getValue()) {
                case "Classic":
                    Fonts.apple18.drawStringWithShadow("S", 4, 4, 0x5499C7);
                    Fonts.apple18.drawStringWithShadow("lack", 10, 4, -1);
                    break;
                case "Backgrounded":
                drawRect(2, 2, 80, 14, 0x80000000);
                Fonts.apple18.drawStringWithShadow("Slack " + Slack.getInstance().getInfo().getVersion(), 4, 4, 0x5499C7);
                Fonts.apple18.drawStringWithShadow(" - " + Minecraft.getDebugFPS(), 53, 4, -1);
                break;
            }
            if (fpsdraw.getValue()) {
                Fonts.apple20.drawStringWithShadow("FPS:  " , 4, 490, 0x5D0C1D);
                Fonts.apple18.drawStringWithShadow(""+Minecraft.getDebugFPS(), 25, 490, -1);
            }

            if (bpsdraw.getValue()) {
                Fonts.apple20.drawStringWithShadow("BPS:  ", 50, 490, 0x5D0C1D);

            }

//        Render2DUtil.drawImage(new ResourceLocation("slack/textures/logo/trans-512.png"), 12, 12, 32, 32, new Color(255, 255, 255, 150));
        arraylistModes.getValue().onRender(e);
    }
}
