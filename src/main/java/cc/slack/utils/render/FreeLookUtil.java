package cc.slack.utils.render;

import cc.slack.utils.client.mc;
import org.lwjgl.opengl.Display;

public class FreeLookUtil extends mc {

    private float cameraYaw;
    private float cameraPitch;
    private boolean freelooking;

    public void overrideMouse() {
        if (mc.getMinecraft().inGameHasFocus && Display.isActive()) {
            mc.getMinecraft().mouseHelper.mouseXYChange();
            float f1 = mc.getGameSettings().mouseSensitivity * 0.6f + 0.2f;
            float f2 = f1 * f1 * f1 * 8.0f;
            float f3 = (float)mc.getMinecraft().mouseHelper.deltaX * f2;
            float f4 = (float)mc.getMinecraft().mouseHelper.deltaY * f2;
            this.cameraYaw += f3 * 0.15f;
            this.cameraPitch -= f4 * 0.15f;
            this.cameraPitch = Math.max(-90.0f, Math.min(90.0f, this.cameraPitch));
        }
    }

    public boolean isFreelooking() {
        if (freelooking) {
            return true;
        }
        this.cameraYaw = mc.getPlayer().rotationYaw;
        this.cameraPitch = mc.getPlayer().rotationPitch;
        return false;
    }

    public float getYaw() {
        return freelooking ? this.cameraYaw : mc.getPlayer().rotationYaw;
    }

    public float getPitch() {
        return freelooking ? this.cameraPitch : mc.getPlayer().rotationPitch;
    }

    public float getPrevYaw() {
        return freelooking  ? this.cameraYaw : mc.getPlayer().prevRotationYaw;
    }

    public float getPrevPitch() {
        return freelooking ? this.cameraPitch : mc.getPlayer().rotationPitch;
    }

    public static void setFreelooking(boolean freelooking) {
        freelooking = freelooking;
    }

}
