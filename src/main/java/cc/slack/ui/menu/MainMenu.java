package cc.slack.ui.menu;

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

    public static int animY;
    private TimeUtil animTimer = new TimeUtil();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mc.getTextureManager().bindTexture(new ResourceLocation("slack/menu/mainmenu.jpg"));
        drawModalRectWithCustomSizedTexture(0, 0,0,0, this.width, this.height, this.width, this.height);

        if (!Minecraft.cacheChunkReloader || !Minecraft.getMinecraft().i34) {
            Gui.drawRect(0, 0, this.width , this.height, new Color(0,0,0,110).getRGB());
            Fonts.apple45.drawString("  lack Client", 16, 30, -1);

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.35 , 0.35, 0.35);
            mc.getTextureManager().bindTexture(imageResource);
            drawModalRectWithCustomSizedTexture(1, 64, 0, 0, 100, 100, 100, 100);
            GlStateManager.popMatrix();

            if (!dmTimer.hasReached(10000))
                Fonts.apple18.drawStringWithShadow(debugMessage, 5, 100, new Color(255, 50, 50).getRGB());

            super.drawScreen(mouseX, mouseY, partialTicks);
            animTimer.reset();
            return;
        }

        if (!animTimer.hasReached(600)) {
            animY = (int) ((1 - Math.pow(1 - (animTimer.elapsed() / 600.0), 4)) * this.height * 0.7);
        } else {
            animY = 0;
        }

        RenderUtil.drawRoundedRect(width / 2 - 70, height / 2 - 80 + animY, width / 2 + 70, height / 2 + 120 + animY, 10, new Color(0, 0, 0, 110).getRGB());

        Fonts.apple20.drawString("Made by Slack Team", 10,  height - 20, -1);


        GlStateManager.pushMatrix();
        GlStateManager.scale(0.35 , 0.35, 0.35);
        mc.getTextureManager().bindTexture(imageResource);
        drawModalRectWithCustomSizedTexture((int) ((width/2) / 0.35) - 50, (int) ((height / 2 - 75) / 0.35), 0, 0, 100, 100, 100, 100);
        GlStateManager.popMatrix();
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

        if (Minecraft.cacheChunkReloader) {
            addButtons();
        } else {

            this.menuList.add(new MainMenuButton(10, width/2, height / 2, decodes("RmV0Y2ggRGlzY29yZCBpZCBmcm9tIGNsaXBib2FyZA==")));
            this.menuList.add(new MainMenuButton(8, width/2 , height / 2 + 85, decodes("Q29weSBId2lk")));
            this.menuList.add(new MainMenuButton(951, width/2, height / 2 + 60, decodes("TG9nIElu")));
        }

        super.initGui();
    }

    @Override
    protected void actionPerformedMenu(MainMenuButton buttonMenu) throws IOException {
        super.actionPerformedMenu(buttonMenu);

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
            this.mc.i34 = true;
            Minecraft.cacheChunkReloader = true;

            animTimer.reset();

            addButtons();
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

    public static String decodes(String encodedInput) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedInput);
        return new String(decodedBytes);
    }

    private void addButtons() {
        this.menuList.add(new MainMenuButton(1, width/2, height / 2 - 40, decodes("U2luZ2xlUGxheWVy")));
        this.menuList.add(new MainMenuButton(2, width/2, height / 2 - 15, decodes("TXVsdGlQbGF5ZXI=")));
        this.menuList.add(new MainMenuButton(3, width/2, height / 2 + 10, decodes("U2V0dGluZ3M=")));
        this.menuList.add(new MainMenuButton(4, width/2, height / 2 + 35, decodes("QWx0IE1hbmFnZXI=")));
        this.menuList.add(new MainMenuButton(6, width/2, height / 2 + 60, decodes("U2h1dGRvd24=")));
        this.menuList.add(new MainMenuButton(7, width/2, height / 2 + 85, decodes("Q2xpZW50IEluZm9ybWF0aW9u")));
    }

}
