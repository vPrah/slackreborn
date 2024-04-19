package cc.zenith.ui.NewCGUI.components;

import cc.zenith.utils.client.MC;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@Setter
public abstract class Components {

    private int posX, posY, width, height, offsetY;
    private Components parent;

    public ScaledResolution getSR() {
        return new ScaledResolution(MC.getMinecraft());
    }

    public abstract void init();

    public abstract void update(int x, int y);

    public abstract void draw(FontRenderer font, int mouseX, int mouseY, float partialTicks);

    public abstract void mouseClicked(int mouseX, int mouseY, int button);

    public abstract void mouseReleased(int mouseX, int mouseY, int state);

    public abstract void keyClicked(char typedChar, int key);

    public abstract void close();
}
