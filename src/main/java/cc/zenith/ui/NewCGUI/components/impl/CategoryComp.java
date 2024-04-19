package cc.zenith.ui.NewCGUI.components.impl;

import cc.zenith.Zenith;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.ui.NewCGUI.components.Components;
import cc.zenith.utils.render.Render2DUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.*;
import java.util.List;

@Getter
@Setter
public class CategoryComp extends Components {

    protected final List<ModuleComp> modules = new ArrayList<>();
    private int dragX, dragY;
    private boolean collapsed, dragging;
    private Category category;

    public CategoryComp(Category category, int posX, int posY, int width, int height) {
        this.category = category;
        setPosX(posX);
        setPosY(posY);
        setWidth(width);
        setHeight(height);
    }

    @Override
    public void init() {
        int offsetY = 1;
        for (Module module : Zenith.getInstance().getModuleManager().getModulesByCategoryABC(category)) {
            ModuleComp moduleComp = new ModuleComp(module, this, offsetY);
            modules.add(moduleComp);
            ++offsetY;
        }
    }

    @Override
    public void update(int x, int y) {
        if (dragging) {
            setPosX(MathHelper.clamp_int(x - dragX, 0, getSR().getScaledWidth() - getWidth()));
            setPosY(MathHelper.clamp_int(y - dragY, 0, getSR().getScaledHeight() - getHeight()));
        }
    }

    @Override
    public void draw(FontRenderer font, int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(getPosX(), getPosY(), getPosX() + getWidth(), getPosY() + getHeight(), getCategory().getColorRGB());

        font.drawStringWithShadow(getCategory().getName(), getPosX() + 5F, getPosY() + 4F, Color.WHITE.getRGB());
        font.drawStringWithShadow(isCollapsed() ? "+" : "-", getPosX() + getWidth() - 10F, getPosY() + 4F, Color.WHITE.getRGB());

        if (!isCollapsed()) {
            modules.forEach(moduleComp -> moduleComp.draw(font, mouseX, mouseY, partialTicks));
        }

        update(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!isCollapsed()) {
            modules.forEach(moduleComp -> moduleComp.mouseClicked(mouseX, mouseY, button));
        }
        if (!Render2DUtil.mouseInArea(mouseX, mouseY, getPosX(), getPosY(), getWidth(), getHeight())) return;
        switch (button) {
            case 0:
                dragging = true;
                dragX = mouseX - getPosX();
                dragY = mouseY - getPosY();
                break;
            case 1:
                collapsed = !collapsed;
                break;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
    }

    @Override
    public void keyClicked(char typedChar, int key) {

    }

    @Override
    public void close() {
        dragging = false;
    }
}
