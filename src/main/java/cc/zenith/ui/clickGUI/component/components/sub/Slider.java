package cc.slack.ui.clickGUI.component.components.sub;

import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.ui.clickGUI.component.Component;
import cc.slack.ui.clickGUI.component.components.Button;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.MathUtil;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.Gui;

public class Slider extends Component {

    private boolean hovered;

    private final NumberValue set;
    private final Button parent;
    private int offset;
    private int x;
    private int y;
    private boolean dragging = false;

    private double renderWidth;

    public Slider(NumberValue value, Button button, int offset) {
        this.set = value;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (int) renderWidth, parent.parent.getY() + offset + 12, hovered ? 0xFF555555 : 0xFF444444);
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xFF111111);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        mc.getFontRenderer().drawStringWithShadow(this.set.getName() + ": " + this.set.getValue(), (parent.parent.getX() * 2 + 15), (parent.parent.getY() + offset + 2) * 2 + 5, -1);

        GL11.glPopMatrix();
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButtonD(mouseX, mouseY) || isMouseOnButtonI(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();

        double diff = Math.min(88, Math.max(0, mouseX - this.x));

        double min = set.getMinimum().doubleValue();
        double max = set.getMaximum().doubleValue();

        Number value = (Number) set.getValue();
        renderWidth = (88) * (value.doubleValue() - min) / (max - min);

        if (dragging) {
            if (diff == 0) {
                set.setValue(set.getMinimum());
            } else {
                double roundMath = (diff / 88) * (max - min) + min;
                if (set.getMinimum() instanceof Integer) {
                    set.setValue((int) MathUtil.round(roundMath, 0));
                } else if (set.getMinimum() instanceof Double) {
                    set.setValue(MathUtil.round(MathUtil.roundToDecimalPlace(roundMath, set.getIncrement().doubleValue()), 2));
                } else if (set.getMinimum() instanceof Float) {
                    set.setValue((float) MathUtil.round(MathUtil.roundToDecimalPlace(roundMath, set.getIncrement().doubleValue()), 2));
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open) {
            dragging = true;
        }
        if (isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
    }

    public boolean isMouseOnButtonD(int x, int y) {
        if (x > this.x && x < this.x + (parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 12) {
            return true;
        }
        return false;
    }

    public boolean isMouseOnButtonI(int x, int y) {
        if (x > this.x + parent.parent.getWidth() / 2 && x < this.x + parent.parent.getWidth() && y > this.y && y < this.y + 12) {
            return true;
        }
        return false;
    }
}
