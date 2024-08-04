package cc.slack.features.modules.impl.render.hud.watermarks.impl;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.impl.render.HUD;
import cc.slack.features.modules.impl.render.hud.watermarks.IWatermarks;
import cc.slack.start.Slack;
import cc.slack.utils.font.Fonts;
import cc.slack.utils.player.PlayerUtil;
import cc.slack.utils.render.ColorUtil;
import cc.slack.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;

import static net.minecraft.client.gui.Gui.drawRect;

public class BackgroundedWatermark implements IWatermarks {
    @Override
    public void onRender(RenderEvent event) {
        renderBackgroundedRound(ColorUtil.getColor(Slack.getInstance().getModuleManager().getInstance(HUD.class).theme.getValue(), 0.15).getRGB(), new Color(255, 255, 255, 255).getRGB(), new Color(1, 1, 1, 100).getRGB());
    }

    @Override
    public void onUpdate(UpdateEvent event) {

    }

    private void renderBackgroundedRound(int themeColor, int whiteColor, int backgroundColor) {
        switch (Slack.getInstance().getModuleManager().getInstance(HUD.class).watermarkFont.getValue()) {
            case "Apple":
                drawBackgroundedAppleText(themeColor, whiteColor, backgroundColor, Slack.getInstance().getModuleManager().getInstance(HUD.class).watermarkroundValue.getValue());
                break;
            case "Poppins":
                drawBackgroundedPoppinsText(themeColor, whiteColor, backgroundColor, Slack.getInstance().getModuleManager().getInstance(HUD.class).watermarkroundValue.getValue());
                break;
            case "Roboto":
                drawBackgroundedRobotoText(themeColor, whiteColor, backgroundColor, Slack.getInstance().getModuleManager().getInstance(HUD.class).watermarkroundValue.getValue());
                break;
        }
    }


    private void drawBackgroundedRobotoText(int themeColor, int whiteColor, int backgroundColor, boolean rounded) {
        int[] positions = calculateRobotoTextPositions();
        int width = positions[positions.length - 1] + 4;

        if (rounded) {
            RenderUtil.drawRoundedRect(2, 2, width + 32, 15, Slack.getInstance().getModuleManager().getInstance(HUD.class).customroundValue.getValue(), backgroundColor);
        } else {
            drawRect(2, 2, width + 32, 15, backgroundColor);
        }

        Fonts.roboto20.drawStringWithShadow("S", positions[0], 5, themeColor);
        Fonts.roboto20.drawStringWithShadow("lack ", positions[1], 5, whiteColor);
        Fonts.roboto18.drawStringWithShadow(" | ", positions[2], 5, whiteColor);
        Fonts.roboto18.drawStringWithShadow((Minecraft.getMinecraft().isIntegratedServerRunning()) ? "SinglePlayer" : PlayerUtil.getRemoteIp(), positions[3], 5, whiteColor);
        Fonts.roboto18.drawStringWithShadow(" | ", positions[4], 5, whiteColor);
        Fonts.roboto18.drawStringWithShadow(Minecraft.getDebugFPS() + " FPS", positions[5], 5, whiteColor);
    }

    private void drawBackgroundedAppleText(int themeColor, int whiteColor, int backgroundColor, boolean rounded) {
        int[] positions = calculateAppleTextPositions();
        int width = positions[positions.length - 1] + 4;

        if (rounded) {
            RenderUtil.drawRoundedRect(2, 2, width + 32, 15, 4.0f, backgroundColor);
        } else {
            drawRect(2, 2, width + 32, 15, backgroundColor);
        }

        Fonts.apple20.drawStringWithShadow("S", positions[0], 5, themeColor);
        Fonts.apple20.drawStringWithShadow("lack ", positions[1], 5, whiteColor);
        Fonts.apple18.drawStringWithShadow(" | ", positions[2], 5, whiteColor);
        Fonts.apple18.drawStringWithShadow((Minecraft.getMinecraft().isIntegratedServerRunning()) ? "SinglePlayer" : PlayerUtil.getRemoteIp(), positions[3], 5, whiteColor);
        Fonts.apple18.drawStringWithShadow(" | ", positions[4], 5, whiteColor);
        Fonts.apple18.drawStringWithShadow(Minecraft.getDebugFPS() + " FPS", positions[5], 5, whiteColor);
    }

    private void drawBackgroundedPoppinsText(int themeColor, int whiteColor, int backgroundColor, boolean rounded) {
        int[] positions = calculatePoppinsTextPositions();
        int width = positions[positions.length - 1] + 4;

        if (rounded) {
            RenderUtil.drawRoundedRect(2, 2, width, 15, 4.0f, backgroundColor);
        } else {
            drawRect(2, 2, width, 15, backgroundColor);
        }

        Fonts.poppins20.drawStringWithShadow("S", positions[0], 5, themeColor);
        Fonts.poppins20.drawStringWithShadow("lack ", positions[1], 5, whiteColor);
        Fonts.poppins18.drawStringWithShadow(" | ", positions[2], 5, whiteColor);
        Fonts.poppins18.drawStringWithShadow("build 022390", positions[3], 5, whiteColor);
        Fonts.poppins18.drawStringWithShadow(" | ", positions[4], 5, whiteColor);
        Fonts.poppins18.drawStringWithShadow(Minecraft.getDebugFPS() + " FPS", positions[5], 5, whiteColor);
    }

    private int[] calculateRobotoTextPositions() {
        int x = 4;
        int[] positions = new int[6];

        positions[0] = x;
        x += Fonts.roboto20.getStringWidth("S") + 1;
        positions[1] = x;
        x += Fonts.roboto20.getStringWidth("lack ") + 1;
        positions[2] = x;
        x += Fonts.roboto18.getStringWidth(" | ") + 1;
        positions[3] = x;
        x += Fonts.roboto18.getStringWidth((Minecraft.getMinecraft().isIntegratedServerRunning()) ? "SinglePlayer" : PlayerUtil.getRemoteIp()) + 1;
        positions[4] = x;
        x += Fonts.roboto18.getStringWidth(" | ") + 1;
        positions[5] = x;

        return positions;
    }

    private int[] calculateAppleTextPositions() {
        int x = 4;
        int[] positions = new int[6];

        positions[0] = x;
        x += Fonts.apple20.getStringWidth("S") + 1;
        positions[1] = x;
        x += Fonts.apple20.getStringWidth("lack ") + 1;
        positions[2] = x;
        x += Fonts.apple18.getStringWidth(" | ") + 1;
        positions[3] = x;
        x += Fonts.apple18.getStringWidth((Minecraft.getMinecraft().isIntegratedServerRunning()) ? "SinglePlayer" : PlayerUtil.getRemoteIp()) + 1;
        positions[4] = x;
        x += Fonts.apple18.getStringWidth(" | ") + 1;
        positions[5] = x;

        return positions;
    }

    private int[] calculatePoppinsTextPositions() {
        int x = 4;
        int[] positions = new int[6];

        positions[0] = x; // Position for "S"
        x += Fonts.poppins20.getStringWidth("S") + 2;
        positions[1] = x; // Position for "lack "
        x += Fonts.poppins20.getStringWidth("lack ") + 2;
        positions[2] = x; // Position for " | "
        x += Fonts.poppins18.getStringWidth(" | ") + 2;
        positions[3] = x; // Position for "build 022390"
        x += Fonts.poppins18.getStringWidth((Minecraft.getMinecraft().isIntegratedServerRunning()) ? "SinglePlayer" : PlayerUtil.getRemoteIp()) + 2;
        positions[4] = x; // Position for " | "
        x += Fonts.poppins18.getStringWidth(" | ") + 2;
        positions[5] = x;

        return positions;

    }

    @Override
    public String toString() {
        return "Background";
    }
}
