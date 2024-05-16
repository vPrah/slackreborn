// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
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


import java.awt.*;

import static java.lang.Math.round;
import static net.minecraft.client.gui.Gui.drawRect;

@ModuleInfo(
        name = "HUD",
        category = Category.RENDER
)

public class HUD extends Module {
    private final ModeValue<IArraylist> arraylistModes = new ModeValue<>("Arraylist", new IArraylist[]{new BasicArrayList(), new Basic2ArrayList()});

    private final ModeValue<String> watermarksmodes = new ModeValue<>("WaterMark", new String[]{"Classic", "Backgrounded"});

    private final BooleanValue fpsdraw = new BooleanValue("FPS Counter", true);
    private final BooleanValue bpsdraw = new BooleanValue("BPS Counter", true);

    private final BooleanValue scaffoldDraw = new BooleanValue("Scaffold Counter", false);

    private int scaffoldTicks = 0;

    public HUD() {
        super();
        addSettings(arraylistModes, watermarksmodes, fpsdraw, bpsdraw, scaffoldDraw);
    }

    @Listen
    public void onUpdate(UpdateEvent e) {
        arraylistModes.getValue().onUpdate(e);
    }

    @Listen
    @SuppressWarnings("unused")
    public void onRender(RenderEvent e) {
        if (e.state != RenderEvent.State.RENDER_2D) return;

        arraylistModes.getValue().onRender(e);

        switch (watermarksmodes.getValue()) {
            case "Classic":
                Fonts.apple18.drawStringWithShadow("S", 4, 4, 0x5499C7);
                Fonts.apple18.drawStringWithShadow("lack", 10, 4, -1);
                break;
            case "Backgrounded":
                drawRect(2, 2, 55 + Fonts.apple18.getStringWidth(" - " + Minecraft.getDebugFPS()), 15, 0x80000000);
                Fonts.apple18.drawStringWithShadow("Slack " + Slack.getInstance().getInfo().getVersion(), 4, 5, 0x5499C7);
                Fonts.apple18.drawStringWithShadow(" - " + Minecraft.getDebugFPS(), 53, 5, -1);
                break;
        }
        if (fpsdraw.getValue()) {
            Fonts.apple18.drawStringWithShadow("FPS:  ", 4, mc.getScaledResolution().getScaledHeight() - 10, 0x5499C7);
            Fonts.apple18.drawStringWithShadow("" + Minecraft.getDebugFPS(), 25, mc.getScaledResolution().getScaledHeight() - 10, -1);
        }

        if (bpsdraw.getValue()) {
            Fonts.apple18.drawStringWithShadow("BPS:  ", 50, mc.getScaledResolution().getScaledHeight() - 10, 0x5499C7);
            Fonts.apple18.drawStringWithShadow(getBPS(), 71, mc.getScaledResolution().getScaledHeight() - 10, -1);

        }

        if (scaffoldDraw.getValue()) {
            if (Slack.getInstance().getModuleManager().getInstance(Scaffold.class).isToggle()) {
                if (scaffoldTicks < 5) scaffoldTicks++;
            } else {
                if (scaffoldTicks > 0) scaffoldTicks--;
            }

            if (scaffoldTicks == 0) return;
            ScaledResolution sr = mc.getScaledResolution();
            if (mc.getPlayer().inventoryContainer.getSlot(mc.getPlayer().inventory.currentItem + 36).getStack() != null) {
                String displayString = mc.getPlayer().inventoryContainer.getSlot(mc.getPlayer().inventory.currentItem + 36).getStack().stackSize + " blocks";
                drawRect((int) ((sr.getScaledWidth() - mc.getFontRenderer().getStringWidth(displayString)) / 2f) - 2,
                        (int) (sr.getScaledHeight() * 3f / 4F - 2f),
                        (int) ((sr.getScaledWidth() + mc.getFontRenderer().getStringWidth(displayString)) / 2f) + 2,
                        (int) (sr.getScaledHeight() * 3f / 4F + 2f),
                        0x80000000);
                mc.getFontRenderer().drawString(displayString,
                        (sr.getScaledWidth() - mc.getFontRenderer().getStringWidth(displayString)) / 2f,
                        sr.getScaledHeight() * 3f / 4F,
                        new Color(255, 255, 255).getRGB(),
                        false);
            }
        }

//        Render2DUtil.drawImage(new ResourceLocation("slack/textures/logo/trans-512.png"), 12, 12, 32, 32, new Color(255, 255, 255, 150));
    }

    private String getBPS() {
        double currentBPS = ((double) round((MovementUtil.getSpeed() * 20) * 100)) / 100;
        return String.format("%.2f", currentBPS);
    }
}
