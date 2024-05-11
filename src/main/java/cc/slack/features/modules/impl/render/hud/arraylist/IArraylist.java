// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render.hud.arraylist;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;

public interface IArraylist {
    void onRender(RenderEvent event);
    void onUpdate(UpdateEvent event);
}
