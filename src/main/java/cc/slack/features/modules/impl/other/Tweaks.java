package cc.slack.features.modules.impl.other;

import cc.slack.events.impl.game.TickEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "Tweaks",
        category = Category.OTHER
)
public class Tweaks extends Module {


    public final BooleanValue noachievement = new BooleanValue("NoAchievement", true);
    public final BooleanValue noblockhitdelay = new BooleanValue("NoBlockHitDelay", false);
    public final BooleanValue noclickdelay = new BooleanValue("NoClickDelay", false);
    public final BooleanValue nojumpdelay = new BooleanValue("NoJumpDelay", false);
    public final NumberValue<Integer> noJumpDelayTicks = new NumberValue<>("JumpDelayTicks", 0, 0, 10, 1);
    public final BooleanValue nobosshealth = new BooleanValue("NoBossHealth", false);
    private final BooleanValue fullbright = new BooleanValue("FullBright", false);

    float prevGamma = -1F;

    public Tweaks() {
        super();
        addSettings(noachievement, noblockhitdelay, noclickdelay, fullbright, nobosshealth, nojumpdelay, noJumpDelayTicks);
    }

    @Override
    public void onEnable() {prevGamma = mc.getGameSettings().gammaSetting;}

    @Listen
    public void onUpdate (UpdateEvent event) {
        if (fullbright.getValue()) {
            if (mc.getGameSettings().gammaSetting <= 100f) mc.getGameSettings().gammaSetting++;
        } else if (prevGamma != -1f) {
            mc.getGameSettings().gammaSetting = prevGamma;
            prevGamma = -1f;
        }
    }

    @Listen
    public void onMotion (MotionEvent event) { if (noblockhitdelay.getValue()) { mc.getPlayerController().blockHitDelay = 0; } }

    @Override
    public void onDisable() {
        if (prevGamma == -1f) return;
        mc.getGameSettings().gammaSetting = prevGamma;
        prevGamma = -1f;
    }
}
