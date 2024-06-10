package cc.slack.ui.menu;

import cc.slack.Slack;
import cc.slack.ui.altmanager.gui.GuiAccountManager;
import cc.slack.utils.client.Login;
import cc.slack.utils.font.Fonts;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

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

        if (!Minecraft.i1i1i1ii1i1i1ii1i1ii1i || !Minecraft.getMinecraft().i34) {
            Gui.drawRect(0, 0, 200 , this.height, new Color(0,0,0,110).getRGB());
            Fonts.apple45.drawString("  lack Client", 16, 30, -1);

            if (!dmTimer.hasReached(10000))
                Fonts.apple18.drawStringWithShadow(debugMessage, 5, 100, new Color(255, 50, 50).getRGB());

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.35 , 0.35, 0.35);
            mc.getTextureManager().bindTexture(imageResource);
            drawModalRectWithCustomSizedTexture(1, 64, 0, 0, 100, 100, 100, 100);
            GlStateManager.popMatrix();

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

        if (Minecraft.i1i1i1ii1i1i1ii1i1ii1i) {
            this.menuList.add(new MainMenuButton(1, - 30, height / 2 - 40, "SinglePlayer"));
            this.menuList.add(new MainMenuButton(2, - 30, height / 2 - 15, "MultiPlayer"));
            this.menuList.add(new MainMenuButton(3, - 30, height / 2 + 10, "Settings"));
            this.menuList.add(new MainMenuButton(4, - 30, height / 2 + 35, "Alt Manager"));
            this.menuList.add(new MainMenuButton(6, - 30, height / 2 + 60, "Shutdown"));
            this.menuList.add(new MainMenuButton(7, - 30, height / 2 + 85, "Client Information"));
        } else {

            if (Slack.getInstance().isNoREQHwid())
                this.menuList.add(new MainMenuButton(1124, width/2 - 80, height / 2 + 40, "Dev login"));

            this.menuList.add(new MainMenuButton(10, width/2 - 80, height / 2, "Fetch Discord id from clipboard"));
            this.menuList.add(new MainMenuButton(8, width/2 - 80, height / 2 + 85, "Copy Hwid"));
            this.menuList.add(new MainMenuButton(951, width/2 - 80, height / 2 + 60, "Log In"));
        }

        super.initGui();
    }

    @Override
    protected void actionPerformedMenu(MainMenuButton buttonMenu) throws IOException {
        super.actionPerformedMenu(buttonMenu);

        if (buttonMenu.id == 1124) {
            this.menuList.clear();
            this.mc.i34 = true;
            Minecraft.i1i1i1ii1i1i1ii1i1ii1i = true;

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
            if (fetchHwid() == "f") {
                setMsg("Failed to fetch hwid");
                return;
            }
            GuiScreen.setClipboardString(fetchHwid());
        }

        if (buttonMenu.id == 951) {

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
            Request request = Login.sendReq(client, hwid, discordId);

            // Execute the request
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {

                    String resp = response.body().string();

                    if (Login.isSuccess(discordId, resp, hwid)) {
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
            Minecraft.i1i1i1ii1i1i1ii1i1ii1i = true;

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
                default:
                case WINDOWS:
                    String computerName = System.getenv("COMPUTERNAME");
                    String userName = System.getProperty("user.name");
                    String processorIdentifier = System.getenv("PROCESSOR_IDENTIFIER");
                    String processorLevel = System.getenv("PROCESSOR_LEVEL");

                    // Concatenate the variables
                    hwid = computerName + userName + processorIdentifier + processorLevel;
                    break;
                case OSX:
                    String useName = System.getProperty("user.name");
                    String osName = System.getProperty("os.name");
                    String osVersion = System.getProperty("os.version");
                    String serialNumber = getSerialNumber();

                    // Concatenate the variables
                    hwid = useName + osName + osVersion + serialNumber;
                    break;
                case LINUX:
                    Process cess = Runtime.getRuntime().exec("sudo dmidecode -s system-uuid");
                    cess.getOutputStream().close();
                    BufferedReader der = new BufferedReader(new InputStreamReader(cess.getInputStream()));

                    // Read the HWID
                    hwid = der.readLine().trim();
                    break;
            }
            if (hwid == "") return "f";
            return generateMD5(hwid) + "f";
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

    private static String getSerialNumber() {
        String serialNumber = "";
        try {
            // Execute the system_profiler command to get the hardware serial number
            Process process = Runtime.getRuntime().exec("system_profiler SPHardwareDataType | awk '/Serial/ { print $4; }'");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            serialNumber = reader.readLine().trim();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return serialNumber;
    }

    private static String generateMD5(String input) throws NoSuchAlgorithmException {
        // Create an MD5 message digest
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

        // Convert byte array into signum representation
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        // Return the hexadecimal string
        return hexString.toString();
    }
}
