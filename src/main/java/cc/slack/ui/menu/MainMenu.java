package cc.slack.ui.menu;

import cc.slack.ui.alt.GuiAltLogin;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class MainMenu extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mc.getTextureManager().bindTexture(new ResourceLocation("slack/menu/test.jpeg"));
        drawModalRectWithCustomSizedTexture(0, 0,0,0, this.width, this.height, this.width, this.height);
        GlStateManager.pushMatrix();
        GlStateManager.translate(width/2f, height/2f - mc.MCfontRenderer.FONT_HEIGHT/2f, 0);
        GlStateManager.scale(3, 3, 1);
        GlStateManager.translate(-(width/1.69f), -(height/1.77),0);
        GlStateManager.popMatrix();
        Gui.drawRect(0, 0, 140 , this.height, new Color(0,0,0,170).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);


    }



    @Override
    public void initGui() {
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
