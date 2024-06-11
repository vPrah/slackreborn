// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.other;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;

@ModuleInfo(
        name = "Targets",
        category = Category.OTHER
)
public class Targets extends Module {

    public final BooleanValue teams = new BooleanValue("Teams", true);
    public final BooleanValue playerTarget = new BooleanValue("Players", true);
    public final BooleanValue animalTarget = new BooleanValue("Animals", false);
    public final BooleanValue mobsTarget = new BooleanValue("Mobs", false);
    public final BooleanValue friendsTarget = new BooleanValue("Friends", false);

    public Targets() {
        addSettings(teams, playerTarget, animalTarget, mobsTarget, friendsTarget);
    }
}
