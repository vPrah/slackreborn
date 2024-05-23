package cc.slack.ui.menu;

import cc.slack.Slack;
import cc.slack.ui.alt.GuiAltLogin;
import cc.slack.utils.font.Fonts;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends GuiScreen {
    private List<Particle> particles = new ArrayList<>();
    private final int particlesDensity = 2000;

    private final ResourceLocation imageResource = new ResourceLocation("slack/menu/menulogo.png");
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mc.getTextureManager().bindTexture(new ResourceLocation("slack/menu/test.jpeg"));
        drawModalRectWithCustomSizedTexture(0, 0,0,0, this.width, this.height, this.width, this.height);

        Gui.drawRect(0, 0, 140 , this.height, new Color(0,0,0,170).getRGB());
    //     GlStateManager.pushMatrix();

     //   GlStateManager.scale(1.4 , 1.4, 1.4);
        Fonts.axi35.drawString(Slack.getInstance().info.name + " Client", 16, 30, -1);
   //     GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);


        GlStateManager.pushMatrix();
        GlStateManager.scale(3 , 3, 3);
        mc.getTextureManager().bindTexture(imageResource);
        drawModalRectWithCustomSizedTexture( this.width / 2 - 50, this.height / 2 - 50, 0, 0, 100, 100, 100, 100);
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

        this.menuList.add(new MainMenuButton(1, - 30, height / 2 - 40, "SinglePlayer"));
        this.menuList.add(new MainMenuButton(2, - 30, height / 2 - 15, "MultiPlayer"));
        this.menuList.add(new MainMenuButton(3, - 30, height / 2 + 10, "Settings"));
        this.menuList.add(new MainMenuButton(4, - 30, height / 2 + 35, "Alt Manager"));
        this.menuList.add(new MainMenuButton(5, - 30, height / 2 + 60, "Shutdown"));
        this.menuList.add(new MainMenuButton(6, - 30, height / 2 + 210, "Client Information"));

        super.initGui();
    }

    @Override
    protected void actionPerformedMenu(MainMenuButton buttonMenu) throws IOException {
        super.actionPerformedMenu(buttonMenu);

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
            mc.displayGuiScreen(new GuiAltLogin(this));
        }

        if(buttonMenu.id == 5) {
            mc.shutdown();
        }

        if(buttonMenu.id == 6) {
            mc.displayGuiScreen(new MenuInfo());
        }
    }
}
