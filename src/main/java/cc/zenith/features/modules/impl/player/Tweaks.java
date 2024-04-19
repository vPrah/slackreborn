package cc.zenith.features.modules.impl.player;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.BooleanValue;
import cc.zenith.utils.client.mc;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "Tweaks",
        category = Category.PLAYER
)
public class Tweaks extends Module {

    public final BooleanValue nobosshealth = new BooleanValue("BossHealth", false);
    private final BooleanValue fullbright = new BooleanValue("FullBright", false);

    float prevGamma = -1F;

    public Tweaks() {
        addSettings(fullbright, nobosshealth);
    }

    @Override
    public void onEnable() {
        prevGamma = mc.getGameSettings().gammaSetting;
    }

    @Listen
    public void onUpdate (UpdateEvent event) {
        if (fullbright.getValue()) {
            if (mc.getGameSettings().gammaSetting <= 100f) mc.getGameSettings().gammaSetting++;
        } else if (prevGamma != -1f) {
            mc.getGameSettings().gammaSetting = prevGamma;
            prevGamma = -1f;
        }
    }

    @Override
    public void onDisable() {
        if (prevGamma == -1f) return;
        mc.getGameSettings().gammaSetting = prevGamma;
        prevGamma = -1f;
    }
}
