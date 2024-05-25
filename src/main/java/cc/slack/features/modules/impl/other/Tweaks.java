// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.other;

import cc.slack.Slack;
import cc.slack.events.impl.game.TickEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "Tweaks",
        category = Category.OTHER
)
public class Tweaks extends Module {


    public final BooleanValue noachievement = new BooleanValue("No Achievement", true);
    public final BooleanValue noblockhitdelay = new BooleanValue("No Block Hit Delay", false);
    public final BooleanValue noclickdelay = new BooleanValue("No Click Delay", true);
    public final BooleanValue nojumpdelay = new BooleanValue("No Jump Delay", false);
    public final NumberValue<Integer> noJumpDelayTicks = new NumberValue<>("Jump Delay Ticks", 0, 0, 10, 1);
    public final BooleanValue nobosshealth = new BooleanValue("No Boss Health", false);
    public final BooleanValue nohurtcam = new BooleanValue("No Hurt Cam", false);
    public final BooleanValue infinitechat = new BooleanValue("Infinite Chat", false);
    private final BooleanValue fullbright = new BooleanValue("FullBright", true);
    private final BooleanValue exitGUIFix = new BooleanValue("Exit Gui Fix", true);
    public final BooleanValue noPumpkin = new BooleanValue("No Pumpkin", true);
    public final BooleanValue customTitle = new BooleanValue("Custom Title", false);

    float prevGamma = -1F;
    boolean wasGUI = false;

    public String status = "";

    public Tweaks() {
        super();
        addSettings(noachievement, noblockhitdelay, noclickdelay, nohurtcam, fullbright, nobosshealth, nojumpdelay, noJumpDelayTicks, exitGUIFix, noPumpkin, customTitle);
    }

    @Override
    public void onEnable() {prevGamma = mc.getGameSettings().gammaSetting;}

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {

        if (fullbright.getValue()) {
            if (mc.getGameSettings().gammaSetting <= 100f) mc.getGameSettings().gammaSetting++;
        } else if (prevGamma != -1f) {
            mc.getGameSettings().gammaSetting = prevGamma;
            prevGamma = -1f;
        }

        if (exitGUIFix.getValue()) {
            if (mc.getCurrentScreen() == null) {
                if (wasGUI) {
                    MovementUtil.updateBinds();
                }
                wasGUI = false;
            } else {
                wasGUI = true;
            }
        }

        if (noclickdelay.getValue()) mc.getMinecraft().leftClickCounter = 0;
    }

    @Listen
    public void onTick (TickEvent event) {
        if (Slack.getInstance().getModuleManager().getInstance(RichPresence.class).started.get()) {
            status = "ON";
        } else {
            status = "OFF";
        }
    }

    @SuppressWarnings("unused")
    @Listen
    public void onMotion (MotionEvent event) { if (noblockhitdelay.getValue()) { mc.getPlayerController().blockHitDelay = 0; } }

    @Override
    public void onDisable() {
        if (prevGamma == -1f) return;
        mc.getGameSettings().gammaSetting = prevGamma;
        prevGamma = -1f;
    }
}
