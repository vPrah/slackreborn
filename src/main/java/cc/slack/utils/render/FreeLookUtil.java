package cc.slack.utils.render;

import cc.slack.utils.client.mc;
import org.lwjgl.opengl.Display;

public class FreeLookUtil extends mc {

    public static float cameraYaw;
    public static float cameraPitch;
    public static boolean freelooking;

    public static void overrideMouse(float f3, float f4) {
        cameraYaw += f3 * 0.15f;
        cameraPitch -= f4 * 0.15f;
        cameraPitch = Math.max(-90.0f, Math.min(90.0f, cameraPitch));
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
        return freelooking ? this.cameraPitch : mc.getPlayer().prevRotationPitch;
    }

    public static void setFreelooking(boolean setFreelook) {
        freelooking = setFreelook;
    }

    public static void enable() {
        setFreelooking(true);
        cameraYaw = mc.getPlayer().rotationYaw;
        cameraPitch = mc.getPlayer().rotationPitch;
    }

    public static void disable() {
        setFreelooking(false);
        cameraYaw = mc.getPlayer().rotationYaw;
        cameraPitch = mc.getPlayer().rotationPitch;
    }

}
