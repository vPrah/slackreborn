package cc.zenith.ui.NewCGUI;

import cc.zenith.features.modules.api.Category;
import cc.zenith.ui.NewCGUI.components.Components;
import cc.zenith.ui.NewCGUI.components.impl.CategoryComp;
import net.minecraft.client.gui.GuiScreen;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransparentClickGUI extends GuiScreen {

    protected static final List<CategoryComp> frames = new ArrayList<>();

    public TransparentClickGUI() {
        int posX = 114;
        int gap = 1;
        for (Category category : Category.values()) {
            CategoryComp catComp = new CategoryComp(category, posX, gap, 110, 15);
            catComp.init();
            frames.add(catComp);
            posX += catComp.getWidth() + gap;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        frames.forEach(categoryComp -> categoryComp.draw(cc.zenith.utils.client.mc.getFontRenderer(), mouseX, mouseY, partialTicks));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        frames.forEach(categoryComp -> categoryComp.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        frames.forEach(categoryComp -> categoryComp.keyClicked(typedChar, keyCode));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        frames.forEach(categoryComp -> categoryComp.mouseReleased(mouseX, mouseY, state));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        frames.forEach(Components::close);
    }
}
