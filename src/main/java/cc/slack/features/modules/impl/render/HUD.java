package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.features.modules.impl.render.hud.arraylist.impl.*;
import cc.slack.utils.render.Render2DUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;


import java.awt.*;

@ModuleInfo(
        name = "HUD",
        category = Category.RENDER,
        key = Keyboard.KEY_M
)

public class HUD extends Module {
    private final ModeValue<IArraylist> arraylistModes = new ModeValue<>("Arraylist", new IArraylist[]{new BasicArrayList(), new Basic2ArrayList()});

    public HUD() {
        super();
        addSettings(arraylistModes);
    }

    @Listen
    @SuppressWarnings("unused")
    public void onRender(RenderEvent e) {
        if (e.state != RenderEvent.State.RENDER_2D) return;
        Render2DUtil.drawImage(new ResourceLocation("slack/textures/logo/trans-512.png"), 12, 12, 32, 32, new Color(255, 255, 255, 150));
        arraylistModes.getValue().onRender(e);
    }
}
