package cc.slack.ui.menu;

import cc.slack.Slack;
import cc.slack.ui.altmanager.gui.GuiAccountManager;
import cc.slack.utils.client.Login;
import cc.slack.utils.font.Fonts;
import cc.slack.utils.other.FileUtil;
import cc.slack.utils.other.TimeUtil;
import cc.slack.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import okhttp3.*;

public class MainMenu extends GuiScreen {
    private List<Particle> particles = new ArrayList<>();
    private final int particlesDensity = 2500;

    private final ResourceLocation imageResource = new ResourceLocation("slack/menu/menulogo.png");

    String debugMessage = "";
    TimeUtil dmTimer = new TimeUtil();

    public static String discordId = "";
    public static String idid = "";

    private static boolean lgi = false;

    public static int animY;
    private TimeUtil animTimer = new TimeUtil();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mc.getTextureManager().bindTexture(new ResourceLocation("slack/menu/mainmenu.jpg"));
        drawModalRectWithCustomSizedTexture(0, 0,0,0, this.width, this.height, this.width, this.height);

        if (!Minecraft.renderChunksCache || !Minecraft.getMinecraft().pointedEffectRenderer) {
            RenderUtil.drawRoundedRect(width / 2 - 115, height / 2 - 115, width / 2 + 115 , this.height / 2 + 115, 15, new Color(44, 43, 43, 50).getRGB());
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.color(1, 1, 1, 1);

            Fonts.poppins18.drawString("Slack Client", width / 2 - 20, height / 2 - 90, new Color(255, 255, 255).getRGB());

            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            RenderUtil.drawImage(imageResource, width / 2 - 45, height / 2 - 100, 15, 27);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();

            if (!dmTimer.hasReached(10000))
                Fonts.apple18.drawStringWithShadow(debugMessage, 5, 5, new Color(255, 50, 50).getRGB());

            super.drawScreen(mouseX, mouseY, partialTicks);

            if (lgi) {
                if (animTimer.hasReached(800)) {
                    Fonts.apple45.drawCenteredStringWithShadow("Logging in...", width / 2f, height / 2f - 20, new Color(255, 255, 255).getRGB());
                    Gui.drawRect(0, 0, width, height, new Color (0, 0, 0, Math.min(100, (int) (animTimer.elapsed() / 3))).getRGB());
                    String hwid = FileUtil.fetchHwid();

                    if (hwid == "f") {
                        setMsg("Could not grab Hwid to verify. Please open a ticket.");
                        return;
                    }

                    if (discordId.length() < 16 || discordId.length() > 20) {
                        setMsg("Invalid Discord id");
                        return;
                    }

                    OkHttpClient client = new OkHttpClient();
                    Request request = Login.sendReq(client, hwid, discordId);


                    // Execute the request
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {

                            String resp = response.body().string();

                            if (Login.isSuccess(discordId, resp, hwid)) {
                                idid = hwid;
                                setMsg(decodes("TG9naW4gU3VjY2Vzc2Z1bA=="));
                            } else {
                                setMsg(decodes("Q3JlZGVudGlhbHMgZGlkbid0IG1hdGNoLg=="));
                                return;
                            }
                        } else {
                            setMsg(decodes("RmFpbGVkIHRvIGdldCByZXNwb25zZSBmcm9tIHNlcnZlci4="));
                            return;
                        }
                    } catch (IOException e) {
                        setMsg(decodes("RmFpbGVkIHRvIGNvbnRhY3Qgc2VydmVyLg=="));
                        return;
                    }

                    this.menuList.clear();
                    this.mc.pointedEffectRenderer = true;
                    Minecraft.renderChunksCache = true;

                    animTimer.reset();

                    addButtons();
                    lgi = false;
                } else {
                    Fonts.apple45.drawCenteredStringWithShadow("Logging in...", width / 2f, height / 2f - 20, new Color(255, 255, 255).getRGB());
                    Gui.drawRect(0, 0, width, height, new Color (0, 0, 0, Math.min(100, (int) (animTimer.elapsed() / 5))).getRGB());
                }
            } else {
                animTimer.reset();
            }
            return;
        }

        if (!animTimer.hasReached(700)) {
            animY = (int) (Math.pow(1 - (animTimer.elapsed() / 700.0), 4) * this.height * 0.7);
        } else {
            animY = 0;
        }

        Fonts.apple20.drawString("Made by Dg636, Vprah, and others with <3", width - 7 - Fonts.apple20.getStringWidth("Made by Dg636, Vprah, and others with <3"),  height - 13, -1);
        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        RenderUtil.drawImage(imageResource, width / 2 - 28, height / 2 - 95 + animY, 46, 80);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        for (Particle particle : particles) {
            particle.update();
            particle.render(mc);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

    }



    @Override
    public void initGui() {

        int numberOfParticles = (this.width * this.height) / particlesDensity;
        particles.clear();
        for (int i = 0; i < numberOfParticles; i++) {
            particles.add(new Particle(this.width, this.height));
        }

        if (Minecraft.renderChunksCache) {
            addButtons();
        } else {

            this.menuList.add(new MainMenuButton(10, width/2 - 100, height / 2 - 48, 200, 19, decodes("RmV0Y2ggRGlzY29yZCBpZCBmcm9tIGNsaXBib2FyZA==")));
            this.menuList.add(new MainMenuButton(8, width/2 - 100, height / 2 - 24, 200, 19, decodes("Q29weSBId2lk")));
            this.menuList.add(new MainMenuButton(951, width/2 - 100, height / 2, 200, 19, decodes("TG9nIElu")));
            this.menuList.add(new MainMenuButton(12, width/2 - 100, height / 2 + 40, 200, 19, decodes("T3BlbiBXZWJzaXRlCg==")));
            this.menuList.add(new MainMenuButton(13, width/2 - 100, height / 2 + 64, 200, 19, decodes("Sm9pbiBPdXIgRGlzY29yZA==")));

        }

        super.initGui();
    }

    @Override
    protected void actionPerformedMenu(MainMenuButton buttonMenu) throws IOException {
        if (lgi) return;
        super.actionPerformedMenu(buttonMenu);

        if (buttonMenu.id == 12) {
            FileUtil.showURL(Slack.getInstance().Website);
        }

        if (buttonMenu.id == 13) {
            FileUtil.showURL(Slack.getInstance().DiscordServer);
        }

        if (buttonMenu.id == 10) {
            discordId = GuiScreen.getClipboardString();
            setMsg("Set discord Id to: " + discordId);
            return;
        }

        if (buttonMenu.id == 8) {
            if (FileUtil.fetchHwid() == "f") {
                setMsg("Failed to fetch hwid");
                return;
            }
            setMsg("Copied to clipboard");
            GuiScreen.setClipboardString(FileUtil.fetchHwid());
        }

        if (buttonMenu.id == 951) {
            lgi = true;
            animTimer.reset();
        } else {
            lgi = false;
        }

        if(buttonMenu.id == 1) {
            mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if(buttonMenu.id == 2) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if(buttonMenu.id == 3) {
            mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if(buttonMenu.id == 4) {
            mc.displayGuiScreen(new GuiAccountManager(this));
        }

        if(buttonMenu.id == 6) {
            mc.shutdown();
        }

        if(buttonMenu.id == 7) {
            mc.displayGuiScreen(new MenuInfo());
        }
    }


    private void setMsg(String m) {
        dmTimer.reset();
        debugMessage = m;
    }

    private String decodes(String encodedInput) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedInput);
        return new String(decodedBytes);
    }

    private void addButtons() {
        this.menuList.add(new MainMenuButton(1, width/2 - 120, height / 2 + 10, 240, 20, decodes("U2luZ2xlUGxheWVy")));
        this.menuList.add(new MainMenuButton(2, width/2 - 120, height / 2 + 35, 240, 20, decodes("TXVsdGlQbGF5ZXI=")));
        this.menuList.add(new MainMenuButton(3, width/2 - 120, height / 2 + 60, 117, 20, decodes("U2V0dGluZ3M=")));
        this.menuList.add(new MainMenuButton(4, width/2 + 3, height / 2 + 60, 117, 20, decodes("QWx0IE1hbmFnZXI=")));
        this.menuList.add(new MainMenuButton(6, 5, height - 25, 60, 20, decodes("U2h1dGRvd24=")));
        this.menuList.add(new MainMenuButton(7, 70, height - 25, 100, 20, decodes("Q2xpZW50IEluZm9ybWF0aW9u")));
    }

}
