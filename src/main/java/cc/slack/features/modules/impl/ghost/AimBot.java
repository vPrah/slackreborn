// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.player.StrafeEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.other.MathUtil;
import cc.slack.utils.player.AttackUtil;
import cc.slack.utils.rotations.RotationUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.EntityLivingBase;

@ModuleInfo(
        name = "AimBot",
        category = Category.GHOST
)
public class AimBot extends Module {

    private final BooleanValue silent = new BooleanValue("Silent Aimbot", false);
    private final BooleanValue silentMoveFix = new BooleanValue("Silent Aimbot Move Fix", true);

    private final NumberValue<Integer> fov = new NumberValue<>("Max FOV", 80, 0, 180, 5);
    private final NumberValue<Integer> minFov = new NumberValue<>("Min FOV", 20, 0, 180, 2);

    private final NumberValue<Float> aimRange = new NumberValue<>("Aim Range", 4.5f, 0f, 8f, 0.1f);

    private final NumberValue<Integer> aimSpeed = new NumberValue<>("Aim Speed", 10, 0, 180, 1);
    private final NumberValue<Float> complimentSpeed = new NumberValue<>("Compliment Percent Speed", 0.2f, 0f, 1f, 0.05f);

    public boolean isSilent;
    private EntityLivingBase target = null;

    public AimBot() {
        addSettings(silent, silentMoveFix, fov, minFov, aimRange, aimSpeed, complimentSpeed);
    }

    private float lastYaw, lastPitch;
    private float[] lastRotation = new float[]{0f, 0f};
    private float complimentYaw, complimentPitch;

    @SuppressWarnings("unused")
    @Listen
    public void onStrafe (StrafeEvent event) {

        if (lastPitch == 0f && lastYaw == 0f) {
            lastPitch = mc.thePlayer.rotationPitch;
            lastYaw = mc.thePlayer.rotationYaw;
        }
        target = AttackUtil.getTarget(aimRange.getValue(), "fov");
        if (target == null) {
           if (isSilent) {
               isSilent = false;
               RotationUtil.disable();
           }
           return;
        }

        float[] targetRotation = RotationUtil.getTargetRotations(target.getEntityBoundingBox(), RotationUtil.TargetRotation.CENTER, 0);
        targetRotation[0] += mc.thePlayer.rotationYaw - lastYaw;
        targetRotation[1] += mc.thePlayer.rotationPitch - lastPitch;
        targetRotation[0] += complimentYaw * (complimentSpeed.getValue() - 1);
        targetRotation[1] += complimentPitch * (complimentSpeed.getValue() - 1);


        lastPitch = mc.thePlayer.rotationPitch;
        lastYaw = mc.thePlayer.rotationYaw;

        complimentYaw = targetRotation[0] - lastRotation[0];
        complimentPitch = targetRotation[1] - lastRotation[1];
        lastRotation = targetRotation;

        if (RotationUtil.getRotationDifference(targetRotation) > fov.getValue()) return;
        if (RotationUtil.getRotationDifference(targetRotation) < minFov.getValue()) return;

        float [] clientRotation = RotationUtil.getLimitedRotation(
               RotationUtil.clientRotation,
               targetRotation,
               (float) Math.min(RotationUtil.getRotationDifference(targetRotation) - minFov.getValue(), aimSpeed.getValue() + MathUtil.getRandomInRange(0.0, (double) aimSpeed.getValue() / 5))
        );

        if (silent.getValue()) {
           RotationUtil.setClientRotation(clientRotation, 1);
           RotationUtil.setStrafeFix(silentMoveFix.getValue(), false);
        } else {
           RotationUtil.setPlayerRotation(clientRotation);
        }
    }
}
