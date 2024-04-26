// Slack Client (discord.gg/slackclient)

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
import cc.slack.features.modules.impl.world.Scaffold;
import cc.slack.utils.client.mc;
import cc.slack.utils.font.Fonts;
import cc.slack.utils.player.MovementUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;


import java.awt.*;

import static java.lang.Math.round;
import static net.minecraft.client.gui.Gui.drawRect;

@ModuleInfo(
        name = "HUD",
        category = Category.RENDER
)

public class HUD extends Module {
    private final ModeValue<IArraylist> arraylistModes = new ModeValue<>("Arraylist", new IArraylist[]{new BasicArrayList(), new Basic2ArrayList()});

    private final ModeValue<String> watermarksmodes = new ModeValue<>("WaterMark-Modes", new String[]{"Classic", "Backgrounded"});

    private final BooleanValue fpsdraw = new BooleanValue("FPS Counter", true);
    private final BooleanValue bpsdraw = new BooleanValue("BPS Counter", true);

    private final BooleanValue scaffoldDraw = new BooleanValue("Scaffold Counter", true);

    private double lastBPS = 0.0;

    private int scaffoldTicks = 0;

    public HUD() {
        super();
        addSettings(arraylistModes, watermarksmodes, fpsdraw, bpsdraw, scaffoldDraw);
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
                Fonts.apple18.drawStringWithShadow("FPS:  " , 4, 490, 0x5499C7);
                Fonts.apple18.drawStringWithShadow(""+Minecraft.getDebugFPS(), 25, 490, -1);
            }

            if (bpsdraw.getValue()) {
                Fonts.apple18.drawStringWithShadow("BPS:  ", 50, 490, 0x5499C7);
                Fonts.apple18.drawStringWithShadow(""+getBPS(), 71, 490, -1);

            }

            if (scaffoldDraw.getValue()) {
                if (Slack.getInstance().getModuleManager().getInstance(Scaffold.class).isToggle()) {
                    if (scaffoldTicks < 5) scaffoldTicks++;
                } else {
                    if (scaffoldTicks > 0) scaffoldTicks--;
                }

                if (scaffoldTicks == 0) return;
                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                mc.getFontRenderer().drawString("Blocks " + String.valueOf(mc.getPlayer().inventoryContainer.getSlot(mc.getPlayer().inventory.currentItem).getStack().stackSize),
                        sr.getScaledWidth() / 1.95f,
                        sr.getScaledHeight() / 2F + 20F - (( 5 - scaffoldTicks) * 5F),
                        new Color(255, 255, 255, scaffoldTicks * 51).getRGB(),
                        true);
            }

//        Render2DUtil.drawImage(new ResourceLocation("slack/textures/logo/trans-512.png"), 12, 12, 32, 32, new Color(255, 255, 255, 150));
        arraylistModes.getValue().onRender(e);
    }

    private Double getBPS() {
        double currentBPS = ((double) round((MovementUtil.getSpeed() * 20) * 100)) / 100;
        double avgBPS = (lastBPS + currentBPS) / 2;
        lastBPS = currentBPS;
        return avgBPS;
    }
}
