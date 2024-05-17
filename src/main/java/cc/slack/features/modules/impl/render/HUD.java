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
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.round;
import static net.minecraft.client.gui.Gui.drawRect;


@ModuleInfo(
        name = "HUD",
        category = Category.RENDER
)

public class HUD extends Module {
    private final ModeValue<IArraylist> arraylistModes = new ModeValue<>("Arraylist", new IArraylist[]{new BasicArrayList(), new Basic2ArrayList()});

    private final ModeValue<String> watermarksmodes = new ModeValue<>("WaterMark", new String[]{"Classic", "Backgrounded"});

    public final BooleanValue notification = new BooleanValue("Notificatons", true);

    private final BooleanValue fpsdraw = new BooleanValue("FPS Counter", true);
    private final BooleanValue bpsdraw = new BooleanValue("BPS Counter", true);

    private final BooleanValue scaffoldDraw = new BooleanValue("Scaffold Counter", false);

    private int scaffoldTicks = 0;

    private ArrayList<String> notText = new ArrayList<>();
    private ArrayList<Long> notEnd = new ArrayList<>();
    private ArrayList<Long> notStart = new ArrayList<>();
    private ArrayList<String> notDetailed = new ArrayList<>();

    public HUD() {
        super();
        addSettings(arraylistModes, watermarksmodes, notification, fpsdraw, bpsdraw, scaffoldDraw);
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
                        (int) (sr.getScaledHeight() * 3f / 4F + mc.getFontRenderer().FONT_HEIGHT + 2f),
                        0x80000000);
                mc.getFontRenderer().drawString(displayString,
                        (sr.getScaledWidth() - mc.getFontRenderer().getStringWidth(displayString)) / 2f,
                        sr.getScaledHeight() * 3f / 4F,
                        new Color(255, 255, 255).getRGB(),
                        false);
            }
        }

        if (notification.getValue()) {
            int y = mc.getScaledResolution().getScaledHeight() - 10;
            for (int i = 0; i < notText.size(); i++) {
                double x = getXpos(notStart.get(i), notEnd.get(i) );
                y -= (int) (Math.pow((1 - x), 2) * 19);

                renderNotification(
                        (int) (mc.getScaledResolution().getScaledWidth() - 10 + 100 * x),
                        y,
                        notText.get(i), notDetailed.get(i));
            }

            ArrayList<Integer> removeList = new ArrayList();

            for (int i = 0; i < notText.size(); i++) {
                if (System.currentTimeMillis() > notEnd.get(i)) {
                    removeList.add(i);
                }
            }

            Collections.reverse(removeList);

            for (Integer i : removeList) {
                notText.remove(i);
                notEnd.remove(i);
                notStart.remove(i);
                notDetailed.remove(i);
            }
        } else {
            notText.clear();
            notEnd.clear();
            notStart.clear();
            notDetailed.clear();
        }

//        Render2DUtil.drawImage(new ResourceLocation("slack/textures/logo/trans-512.png"), 12, 12, 32, 32, new Color(255, 255, 255, 150));
    }

    private String getBPS() {
        double currentBPS = ((double) round((MovementUtil.getSpeed() * 20) * 100)) / 100;
        return String.format("%.2f", currentBPS);
    }

    private void renderNotification(int x, int y, String bigText, String smallText) {
        drawRect(x - 6 - mc.getFontRenderer().getStringWidth(bigText),
                y - 6 - mc.getFontRenderer().FONT_HEIGHT,
                x,
                y,
                new Color(50,50,50).getRGB());
        mc.getFontRenderer().drawString(
                bigText,
                x - 3 - mc.getFontRenderer().getStringWidth(bigText),
                y - 3 - mc.getFontRenderer().FONT_HEIGHT,
                new Color(255,255,255).getRGB());
    }

    private double getXpos(Long startTime, Long endTime) {
        if (endTime - System.currentTimeMillis() < 300L) {
            return Math.pow( 1 - (endTime - System.currentTimeMillis()) / 300f, 3);
        } else if (System.currentTimeMillis() - startTime < 300L) {
            return 1 - Math.pow(System.currentTimeMillis() - startTime / 300f, 3);
        } else {
            return 0.0;
        }
    }

    public void addNotification(String bigText, String smallText, Long duration) {
        if (!notification.getValue()) return;
        notText.add(bigText);
        notEnd.add(System.currentTimeMillis() + duration);
        notStart.add( System.currentTimeMillis());
        notDetailed.add(smallText);
    }
}
