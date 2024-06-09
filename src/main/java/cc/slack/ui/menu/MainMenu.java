package cc.slack.ui.menu;

import cc.slack.Slack;
import cc.slack.ui.alt.GuiAltLogin;
import cc.slack.ui.altmanager.gui.GuiAccountManager;
import cc.slack.utils.client.mc;
import cc.slack.utils.font.Fonts;
import cc.slack.utils.other.MathUtil;
import cc.slack.utils.other.TimeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp

public class MainMenu extends GuiScreen {
    private List<Particle> particles = new ArrayList<>();
    private final int particlesDensity = 2500;

    private final ResourceLocation imageResource = new ResourceLocation("slack/menu/menulogo.png");

    String debugMessage = "";
    TimeUtil dmTimer = new TimeUtil();

    String discordId = "";

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        mc.getTextureManager().bindTexture(new ResourceLocation("slack/menu/mainmenu.jpg"));
        drawModalRectWithCustomSizedTexture(0, 0,0,0, this.width, this.height, this.width, this.height);

        if (!Minecraft.isLoggedIn || !Minecraft.getMinecraft().i34) {
            Gui.drawRect(0, 0, 200 , this.height, new Color(0,0,0,110).getRGB());
            Fonts.apple45.drawString("  lack Client", 16, 30, -1);

            if (!dmTimer.hasReached(10000))
                Fonts.apple18.drawStringWithShadow(debugMessage, 5, 50, new Color(255, 50, 50).getRGB());

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.35 , 0.35, 0.35);
            mc.getTextureManager().bindTexture(imageResource);
            drawModalRectWithCustomSizedTexture(1, 64, 0, 0, 100, 100, 100, 100);
            GlStateManager.popMatrix();

            super.drawScreen(mouseX, mouseY, partialTicks);
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

        this.menuList.add(new MainMenuButton(8,- 30, height / 2 + 85, "Copy Hwid"));
        this.menuList.add(new MainMenuButton(9,- 30, height / 2 + 60, "Log In"));

        super.initGui();
    }

    @Override
    protected void actionPerformedMenu(MainMenuButton buttonMenu) throws IOException {
        super.actionPerformedMenu(buttonMenu);

        if (buttonMenu.id == 8) {
            if (fetchHwid() == "f") {
                return;
            }
            GuiScreen.setClipboardString(fetchHwid());
        }

        if (buttonMenu.id == 9) {

            if (discordId.length() != 19) {
                setMsg("Invalid Discord id");
                return;
            }

            String hwid = fetchHwid();

            if (hwid == "f") {
                setMsg("Could not grab Hwid to verify. Please open a ticket.");
                return;
            }

            OkHttpClient client = new OkHttpClient();

            // Define JSON data
            String json = "{\"input\": \"Hello, Flask!\"}";

            // Create a RequestBody
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

            // Create a POST request
            Request request = new Request.Builder()
                    .url("http://127.0.0.1:5000/custom")
                    .post(body)
                    .build();

            // Execute the request
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    System.out.println("Response from server:");
                    System.out.println(response.body().string());
                } else {
                    System.out.println("Request failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.menuList.clear();
            this.mc.i34 = true;
            Minecraft.isLoggedIn = true;

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

    private String fetchHwid() {
        try {
            String hwid = "";
            switch (Util.getOSType()) {
                case WINDOWS:
                    Process process = Runtime.getRuntime().exec("wmic csproduct get UUID");
                    process.getOutputStream().close();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    reader.readLine();
                    hwid = reader.readLine().trim();
                    break;
                case OSX:
                    Process pr = Runtime.getRuntime().exec("system_profiler SPHardwareDataType | awk '/UUID/ { print $3; }'");
                    pr.getOutputStream().close();
                    BufferedReader re = new BufferedReader(new InputStreamReader(pr.getInputStream()));

                    hwid = re.readLine().trim();
                    break;
                case LINUX:
                    Process cess = Runtime.getRuntime().exec("sudo dmidecode -s system-uuid");
                    cess.getOutputStream().close();
                    BufferedReader der = new BufferedReader(new InputStreamReader(cess.getInputStream()));

                    // Read the HWID
                    hwid = der.readLine().trim();
                    break;
            }
            return hwid + "slc";
        } catch (Exception e) {
            e.printStackTrace();
        }

        dmTimer.reset();
        debugMessage = "Failed to fetch Hwid. Please open a ticket.";

        return "f";
    }

    private void setMsg(String m) {
        dmTimer.reset();
        debugMessage = m;
    }
}
