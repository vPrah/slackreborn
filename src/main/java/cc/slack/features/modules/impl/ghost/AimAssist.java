// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.AttackUtil;
import cc.slack.utils.player.RotationUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

@ModuleInfo(
        name = "AimAssist",
        category = Category.GHOST
)
public class AimAssist extends Module {

    private final BooleanValue lowerSens = new BooleanValue("Lower Sensitivity On Target", true);
    private final NumberValue<Float> lowerSensAmount = new NumberValue<>("Lowered Sensitity Percentage", 0.6f, 0f, 1f, 0.1f);
    private final BooleanValue accelSens = new BooleanValue("Dynamic Acceleration", true);

    private float prevSens;
    private boolean wasHovering = false;

    private float prevDist, currDist;
    private float[] prevRot;
    private boolean wasAccel = false;

    public AimAssist() {
        addSettings(lowerSens, lowerSensAmount, accelSens);
    }

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {
        if (lowerSens.getValue() && !wasAccel) {
            if (mc.getMinecraft().objectMouseOver.entityHit != null) {
                if (!wasHovering) {
                    wasHovering = true;
                    prevSens = mc.getGameSettings().mouseSensitivity;
                    mc.getGameSettings().mouseSensitivity = prevSens * lowerSensAmount.getValue();
                }
            } else {
                if (wasHovering) {
                    wasHovering = false;
                    mc.getGameSettings().mouseSensitivity = prevSens;
                }
            }
        }
        if (accelSens.getValue()) {
            if (mc.getMinecraft().objectMouseOver.entityHit == null) {
                EntityLivingBase target = AttackUtil.getTarget(4.0, "FOV");
                if (target == null) {
                    wasAccel = false;
                    mc.getGameSettings().mouseSensitivity = prevSens;
                } else {
                    if (wasAccel) {
                        prevDist = currDist;
                        currDist = (float) RotationUtil.getRotationDifference((Entity) target);
                        if (RotationUtil.getRotationDifference(prevRot) * 0.7 < currDist - prevDist) {
                            mc.getGameSettings().mouseSensitivity = prevSens * 1.2f;
                        } else {
                            mc.getGameSettings().mouseSensitivity = prevSens;
                        }

                        prevRot = new float[] {mc.getPlayer().rotationYaw, mc.getPlayer().rotationPitch};
                    } else {
                        prevRot = new float[] {mc.getPlayer().rotationYaw, mc.getPlayer().rotationPitch};
                        prevDist = (float) RotationUtil.getRotationDifference((Entity) target);
                        currDist = prevDist;
                        wasAccel = true;
                        prevSens = mc.getGameSettings().mouseSensitivity;
                    }
                }
            } else {
                if (wasHovering) {
                    prevSens = prevSens / 1.2f;
                } else {
                    if (wasAccel) {
                        mc.getGameSettings().mouseSensitivity = prevSens;
                    }
                    prevSens = mc.getGameSettings().mouseSensitivity;
                }
            }
        }
    }
}
