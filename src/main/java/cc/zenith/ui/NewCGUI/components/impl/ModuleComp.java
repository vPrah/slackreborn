package cc.zenith.ui.NewCGUI.components.impl;

import cc.zenith.features.modules.api.Module;
import cc.zenith.ui.NewCGUI.components.Components;
import cc.zenith.utils.other.PrintUtil;
import cc.zenith.utils.render.Render2DUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import java.awt.*;

@Getter
@Setter
public class ModuleComp extends Components {

    private final Module module;
    private boolean expanded;

    public ModuleComp(Module module, Components parent, int offsetY) {
        this.module = module;
        setParent(parent);
        setOffsetY(offsetY);
    }

    @Override
    public void init() {

    }

    @Override
    public void update(int x, int y) {

    }

    @Override
    public void draw(FontRenderer font, int mouseX, int mouseY, float partialTicks) {
        int boxPosY = getParent().getPosY() + (getParent().getHeight() * getOffsetY());

        Gui.drawRect(getParent().getPosX(), boxPosY, getParent().getPosX() + getParent().getWidth(), boxPosY + getParent().getHeight(), new Color(0, 0, 0, 100).getRGB());
        font.drawStringWithShadow(module.getDisplayName(), getParent().getPosX() + 5F, boxPosY + 4F, Color.WHITE.getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!Render2DUtil.mouseInArea(mouseX, mouseY, getParent().getPosX(), getParent().getPosY() + (getParent().getHeight() * getOffsetY()), getParent().getWidth(), getParent().getHeight())) return;
        switch (button) {
            case 0:
                module.toggle();
                break;
            case 1:
                expanded = !expanded;
                PrintUtil.message(module.getDisplayName() + " " + expanded);
                break;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    @Override
    public void keyClicked(char typedChar, int key) {

    }

    @Override
    public void close() {

    }
}
