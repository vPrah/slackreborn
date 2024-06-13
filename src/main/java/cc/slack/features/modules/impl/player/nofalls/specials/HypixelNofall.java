// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.player.nofalls.specials;


import cc.slack.events.State;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.impl.player.nofalls.INoFall;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.PlayerUtil;

public class HypixelNofall implements INoFall {

    private Boolean dmgFall;

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().fallDistance > 4) {
            dmgFall = true;
        }
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (event.getState() != State.PRE) return;

        if (mc.getPlayer().onGround && dmgFall) {
            event.setGround(false);
            mc.getPlayer().fallDistance = 0;
            mc.getPlayer().motionY = PlayerUtil.getJumpHeight();
            dmgFall = false;
        }

    }

    public String toString() {
        return "Hypixel";
    }
}
