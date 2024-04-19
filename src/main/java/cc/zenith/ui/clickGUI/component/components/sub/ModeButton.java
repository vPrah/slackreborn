package cc.zenith.ui.clickGUI.component.components.sub;

import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.utils.client.mc;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.Gui;
import cc.zenith.ui.clickGUI.component.Component;
import cc.zenith.ui.clickGUI.component.components.Button;
import cc.zenith.features.modules.api.Module;

public class ModeButton extends Component {

    private boolean hovered;
    private final Button parent;
    private final ModeValue set;
    private int offset;
    private int x;
    private int y;
    private final Module mod;

    private int modeIndex;

    public ModeButton(ModeValue set, Button button, Module mod, int offset) {
        this.set = set;
        this.parent = button;
        this.mod = mod;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
        this.modeIndex = 0;
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xFF111111);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        String prefix = set.getName() == null ? "Mode: " : set.getName() + " Mode: ";
        mc.getFontRenderer().drawStringWithShadow(prefix + set.getValue().toString(), (parent.parent.getX() + 7) * 2, (parent.parent.getY() + offset + 2) * 2 + 5, -1);
        GL11.glPopMatrix();
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && this.parent.open) {
            int maxIndex = set.getModes().length - 1;
            switch (button) {
                case 0:
                    if (modeIndex + 1 > maxIndex)
                        modeIndex = 0;
                    else
                        modeIndex++;
                    break;
                case 1:
                    if (modeIndex - 1 < 0)
                        modeIndex = maxIndex;
                    else
                        modeIndex--;
                    break;
            }

            set.setValueFromString(set.getModes()[modeIndex].toString());
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        if (x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12) {
            return true;
        }
        return false;
    }
}
