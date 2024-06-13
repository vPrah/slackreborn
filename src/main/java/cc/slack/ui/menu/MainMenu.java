package cc.slack.ui.menu;

import cc.slack.ui.altmanager.gui.GuiAccountManager;
import cc.slack.utils.client.Login;
import cc.slack.utils.font.Fonts;
import cc.slack.utils.other.FileUtil;
import cc.slack.utils.other.TimeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

import static cc.slack.utils.client.Login.sha256;

public class MainMenu extends GuiScreen {
    private List<Particle> particles = new ArrayList<>();
    private final int particlesDensity = 2500;

    private final ResourceLocation imageResource = new ResourceLocation("slack/menu/menulogo.png");

    String debugMessage = "";
    TimeUtil dmTimer = new TimeUtil();

    public static String discordId = "";
    public static String idid = "";

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
            return;
        }

        Gui.drawRect(0, 0, 140 , this.height, new Color(0,0,0,110).getRGB());

        Fonts.apple45.drawString("  lack Client", 16, 30, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);


        GlStateManager.pushMatrix();
        GlStateManager.scale(0.35 , 0.35, 0.35);
        mc.getTextureManager().bindTexture(imageResource);
        drawModalRectWithCustomSizedTexture(1, 64, 0, 0, 100, 100, 100, 100);
        GlStateManager.popMatrix();
        for (Particle particle : particles) {
            particle.update();
            particle.render(mc);
        }

    }



    @Override
    public void initGui() {

        int numberOfParticles = (this.width * this.height) / particlesDensity;
        particles.clear();
        for (int i = 0; i < numberOfParticles; i++) {
            particles.add(new Particle(this.width, this.height));
        }

        if (Minecraft.cacheChunkReloader) {
            this.menuList.add(new MainMenuButton(1, - 30, height / 2 - 40, "SinglePlayer"));
            this.menuList.add(new MainMenuButton(2, - 30, height / 2 - 15, "MultiPlayer"));
            this.menuList.add(new MainMenuButton(3, - 30, height / 2 + 10, "Settings"));
            this.menuList.add(new MainMenuButton(4, - 30, height / 2 + 35, "Alt Manager"));
            this.menuList.add(new MainMenuButton(6, - 30, height / 2 + 60, "Shutdown"));
            this.menuList.add(new MainMenuButton(7, - 30, height / 2 + 85, "Client Information"));
        } else {

            // hard code
            this.menuList.add(new MainMenuButton(1124, width/2 - 120, height / 2 + 40, "Dev login"));

            this.menuList.add(new MainMenuButton(10, width/2 - 120, height / 2, "Fetch Discord id from clipboard"));
            this.menuList.add(new MainMenuButton(8, width/2 - 120, height / 2 + 85, "Copy Hwid"));
            this.menuList.add(new MainMenuButton(951, width/2 - 120, height / 2 + 60, "Log In"));
        }

        super.initGui();
    }

    @Override
    protected void actionPerformedMenu(MainMenuButton buttonMenu) throws IOException {
        super.actionPerformedMenu(buttonMenu);

        if (buttonMenu.id == 1124) {
            this.menuList.clear();
            this.mc.i34 = true;
            Minecraft.cacheChunkReloader = true;

            this.menuList.add(new MainMenuButton(1, - 30, height / 2 - 40, "SinglePlayer"));
            this.menuList.add(new MainMenuButton(2, - 30, height / 2 - 15, "MultiPlayer"));
            this.menuList.add(new MainMenuButton(3, - 30, height / 2 + 10, "Settings"));
            this.menuList.add(new MainMenuButton(4, - 30, height / 2 + 35, "Alt Manager"));
            this.menuList.add(new MainMenuButton(6, - 30, height / 2 + 60, "Shutdown"));
            this.menuList.add(new MainMenuButton(7, - 30, height / 2 + 85, "Client Information"));
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
            GuiScreen.setClipboardString(FileUtil.fetchHwid());
        }

        if (buttonMenu.id == 951) {

            if (discordId.length() < 16 || discordId.length() > 20) {
                setMsg("Invalid Discord id");
                return;
            }

            String hwid = FileUtil.fetchHwid();

            if (hwid == "f") {
                setMsg("Could not grab Hwid to verify. Please open a ticket.");
                return;
            }

            // hard code dev mode
            discordId = "18571";
            String re = sha256("true" + discordId);

            if (Login.isSuccess(discordId, re, hwid)) {
                idid = hwid;

                this.menuList.clear();
                this.mc.i34 = true;
                Minecraft.cacheChunkReloader = true;

                this.menuList.add(new MainMenuButton(1, - 30, height / 2 - 40, "SinglePlayer"));
                this.menuList.add(new MainMenuButton(2, - 30, height / 2 - 15, "MultiPlayer"));
                this.menuList.add(new MainMenuButton(3, - 30, height / 2 + 10, "Settings"));
                this.menuList.add(new MainMenuButton(4, - 30, height / 2 + 35, "Alt Manager"));
                this.menuList.add(new MainMenuButton(6, - 30, height / 2 + 60, "Shutdown"));
                this.menuList.add(new MainMenuButton(7, - 30, height / 2 + 85, "Client Information"));
            }
            // hard code dev mode

            OkHttpClient client = new OkHttpClient();
            Request request = Login.sendReq(client, hwid, discordId);


            // Execute the request
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {

                    String resp = response.body().string();

                    if (Login.isSuccess(discordId, resp, hwid)) {
                        idid = hwid;
                        setMsg("Login Successful");
                    } else {
                        setMsg("Credentials didn't match. " + resp);
                        return;
                    }
                } else {
                    setMsg("Failed to get response from server.");
                    return;
                }
            } catch (IOException e) {
                setMsg("Failed to contact server");
                return;
            }

            this.menuList.clear();
            this.mc.i34 = true;
            Minecraft.cacheChunkReloader = true;

            this.menuList.add(new MainMenuButton(1, - 30, height / 2 - 40, "SinglePlayer"));
            this.menuList.add(new MainMenuButton(2, - 30, height / 2 - 15, "MultiPlayer"));
            this.menuList.add(new MainMenuButton(3, - 30, height / 2 + 10, "Settings"));
            this.menuList.add(new MainMenuButton(4, - 30, height / 2 + 35, "Alt Manager"));
            this.menuList.add(new MainMenuButton(6, - 30, height / 2 + 60, "Shutdown"));
            this.menuList.add(new MainMenuButton(7, - 30, height / 2 + 85, "Client Information"));
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
}
