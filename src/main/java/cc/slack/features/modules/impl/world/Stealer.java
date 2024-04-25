// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.world;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "Stealer",
        category = Category.WORLD
)
public class Stealer extends Module {

    @Listen
    public void onUpdate (UpdateEvent event) {
        }
    }
