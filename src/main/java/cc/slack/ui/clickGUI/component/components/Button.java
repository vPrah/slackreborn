package cc.slack.ui.clickGUI.component.components;

import java.awt.Color;
import java.util.ArrayList;

import cc.slack.features.modules.api.settings.Value;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.ui.clickGUI.component.components.sub.Checkbox;
import cc.slack.ui.clickGUI.component.components.sub.Slider;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;

import cc.slack.ui.clickGUI.component.Component;
import cc.slack.ui.clickGUI.component.Frame;
import cc.slack.ui.clickGUI.component.components.sub.Keybind;
import cc.slack.ui.clickGUI.component.components.sub.ModeButton;
import cc.slack.features.modules.api.Module;

public class Button extends Component {

    public Module mod;
    public Frame parent;
    public int offset;
    private boolean isHovered;
    private final ArrayList<Component> subcomponents;
    public boolean open;

    public Button(Module mod, Frame parent, int offset) {
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
        this.subcomponents = new ArrayList<>();
        this.open = false;
        int opY = offset + 12;
        if (mod.getSetting() != null) {
            for (Value value : mod.getSetting()) {
                if (value instanceof ModeValue) {
                    this.subcomponents.add(new ModeButton((ModeValue) value, this, mod, opY));
                    opY += 12;
                }
                if (value instanceof NumberValue) {
                    this.subcomponents.add(new Slider((NumberValue) value, this, opY));
                    opY += 12;
                }
                if (value instanceof BooleanValue) {
                    this.subcomponents.add(new Checkbox((BooleanValue) value, this, opY));
                    opY += 12;
                }
            }
        }
        this.subcomponents.add(new Keybind(this, opY));
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
        int opY = offset + 12;
        for (Component comp : this.subcomponents) {
            comp.setOff(opY);
            opY += 12;
        }
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? (this.mod.isToggle() ? new Color(0xFF222222).darker().getRGB() : 0xFF222222) : (this.mod.isToggle() ? new Color(14, 14, 14).getRGB() : 0xFF111111));
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Minecraft.getFontRenderer().drawStringWithShadow(this.mod.getName(), (parent.getX() + 2) * 2, (parent.getY() + offset + 2) * 2 + 4, this.mod.isToggle() ? 0x999999 : -1);
        if (this.subcomponents.size() > 2)
            Minecraft.getFontRenderer().drawStringWithShadow(this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 10) * 2, (parent.getY() + offset + 2) * 2 + 4, -1);
        GL11.glPopMatrix();
        if (this.open) {
            if (!this.subcomponents.isEmpty()) {
                this.subcomponents.forEach(Component::renderComponent);
                Gui.drawRect(parent.getX() + 2, parent.getY() + this.offset + 12, parent.getX() + 3, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 12), -379081); //TODO: Get Cat Color
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.open) {
            return (12 * (this.subcomponents.size() + 1));
        }
        return 12;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = isMouseOnButton(mouseX, mouseY);
        if (!this.subcomponents.isEmpty()) {
            this.subcomponents.forEach(comp -> comp.updateComponent(mouseX, mouseY));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY)) {
            switch (button) {
                case 0:
                    this.mod.toggle();
                    break;
                case 1:
                    this.open = !this.open;
                    this.parent.refresh();
                    break;
            }
        }
        this.subcomponents.forEach(comp -> comp.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.subcomponents.forEach(comp -> comp.mouseReleased(mouseX, mouseY, mouseButton));
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        this.subcomponents.forEach(comp -> comp.keyTyped(typedChar, key));
    }

    public boolean isMouseOnButton(int x, int y) {
        if (x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset) {
            return true;
        }
        return false;
    }
}
