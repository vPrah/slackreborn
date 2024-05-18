package cc.slack.features.modules.impl.player;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.MathUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(
        name = "SpeedMine",
        category = Category.PLAYER
)
public class SpeedMine extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Vanilla", "Instant", "NCP"});
    private final NumberValue<Double> speed = new NumberValue<>("Speed", 1.0D, 0.1D, 2.0D, 0.1D);

    public SpeedMine() {
        addSettings(mode, speed);
    }

    @Listen
    public void onUpdate (UpdateEvent event) {
        switch (mode.getValue()) {
            case "Vanilla":
                mc.getPlayerController().curBlockDamageMP *= speed.getValue();
            break;
            case "Instant":
                if (mc.getPlayerController().curBlockDamageMP > 0 &&
                        mc.getGameSettings().keyBindAttack.pressed && mc.getMinecraft().objectMouseOver != null &&
                        mc.getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK &&
                        mc.getMinecraft().objectMouseOver.getBlockPos() != null) {
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            mc.getMinecraft().objectMouseOver.getBlockPos(), mc.getMinecraft().objectMouseOver.sideHit));
                }
            break;
            case "NCP":
                if (mc.getGameSettings().keyBindAttack.pressed && (mc.getMinecraft().objectMouseOver != null &&
                        mc.getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK &&
                        mc.getMinecraft().objectMouseOver.getBlockPos() != null)) {
                    if (mc.getPlayerController().curBlockDamageMP >= 0.5f && !mc.getPlayer().isDead) {
                        mc.getPlayerController().curBlockDamageMP += (MathUtil.getDifference(mc.getPlayerController().curBlockDamageMP, 1.0f) * 0.7f);
                    }
                }
            break;
        }
    }

}
