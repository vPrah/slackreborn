package cc.slack.features.modules.impl.player;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "AutoRespawn",
        category = Category.PLAYER
)
public class AutoRespawn extends Module {

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {
        if (!mc.getPlayer().isEntityAlive()) {
            mc.getPlayer().respawnPlayer();
        }
    }

}
