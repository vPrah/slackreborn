package cc.slack.ui.menu;

import cc.slack.Slack;
import cc.slack.ui.alt.GuiAltLogin;
import cc.slack.ui.clickGUI.ClickGui;
import cc.slack.utils.other.FileUtil;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class MenuInfo extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mc.getTextureManager().bindTexture(new ResourceLocation("slack/menu/test.jpeg"));
        drawModalRectWithCustomSizedTexture(0, 0,0,0, this.width, this.height, this.width, this.height);
        GlStateManager.pushMatrix();
        GlStateManager.translate(width/2f, height/2f - mc.MCfontRenderer.FONT_HEIGHT/2f, 0);
        GlStateManager.scale(3, 3, 1);
        GlStateManager.translate(-(width/1.69f), -(height/1.77),0);
        GlStateManager.popMatrix();
        Gui.drawRect(398, 100, 608 , 400, new Color(0,0,0,170).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);


    }


    @Override
    public void initGui() {
        this.menuList.add(new MainMenuButton(1, + 405, height / 2 - 40, "Show ClickGUI"));
        this.menuList.add(new MainMenuButton(2, + 405, height / 2 - 15, "Join Discord Server"));
        this.menuList.add(new MainMenuButton(3, + 405, height / 2 + 10, "Check Website"));
        this.menuList.add(new MainMenuButton(4, + 405, height / 2 + 35, "Github Org"));
        this.menuList.add(new MainMenuButton(5, + 405, height / 2 + 60, "Developers Team"));


        super.initGui();
    }

    @Override
    protected void actionPerformedMenu(MainMenuButton buttonMenu) throws IOException {
        super.actionPerformedMenu(buttonMenu);

        if(buttonMenu.id == 1) {
            mc.displayGuiScreen(new ClickGui());
        }

        if(buttonMenu.id == 2) {
            FileUtil.showURL(Slack.getInstance().DiscordServer);
        }

        if(buttonMenu.id == 3) {
            FileUtil.showURL(Slack.getInstance().Website);
        }

        if(buttonMenu.id == 4) {
            FileUtil.showURL(Slack.getInstance().GitOrg);
        }

        if(buttonMenu.id == 5) {
            // I need a menu with All developers of Slack Client (With their information)
        }
    }

}
