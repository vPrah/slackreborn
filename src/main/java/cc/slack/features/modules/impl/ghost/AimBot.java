// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.other.MathUtil;
import cc.slack.utils.player.AttackUtil;
import cc.slack.utils.player.RotationUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.EntityLivingBase;

@ModuleInfo(
        name = "AimBot",
        category = Category.GHOST
)
public class AimBot extends Module {

    private final BooleanValue silent = new BooleanValue("Silent Aimbot", false);
    private final BooleanValue silentMoveFix = new BooleanValue("Silent Aimbot Move Fix", true);

    private final NumberValue<Integer> fov = new NumberValue<>("FOV", 80, 0, 180, 5);
    private final NumberValue<Float> aimRange = new NumberValue<>("Aim Range", 4.5f, 0f, 8f, 0.1f);

    private final NumberValue<Integer> aimSpeed = new NumberValue<>("Aim Speed", 10, 0, 180, 1);

    public boolean isSilent;
    private EntityLivingBase target = null;

    public AimBot() {
        addSettings(silent, silentMoveFix, fov, aimRange, aimSpeed);
    }

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {
       target = AttackUtil.getTarget(aimRange.getValue(), "fov");
       if (target == null) {
           if (isSilent) {
               isSilent = false;
               RotationUtil.disable();
           }
           return;
       }

       float[] targetRotation = RotationUtil.getTargetRotations(target.getEntityBoundingBox(), RotationUtil.TargetRotation.MIDDLE, 0.1);

       if (RotationUtil.getRotationDifference(targetRotation) > fov.getValue()) return;

       float [] clientRotation = RotationUtil.getLimitedRotation(
               RotationUtil.clientRotation,
               targetRotation,
               (float) aimSpeed.getValue() + (float) MathUtil.getRandomInRange(0.0, (double) aimSpeed.getValue() / 5)
       );

       if (silent.getValue()) {
           RotationUtil.setClientRotation(clientRotation, 1);
           if (silentMoveFix.getValue()) RotationUtil.setStrafeFix(true, false);
       } else {
           RotationUtil.setPlayerRotation(clientRotation);
       }
    }
}
