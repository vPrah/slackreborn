// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.player.nofalls.specials;

import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.impl.player.nofalls.INoFall;
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VulcanNofall implements INoFall {

    int count;
    boolean isFixed;

    @Override
    public void onEnable() {
        count = 0;
        isFixed = false;
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (mc.getPlayer().onGround && isFixed) {
            isFixed = false;
            count = 0;
            mc.getTimer().timerSpeed = 1.0F;
        }

        if (mc.getPlayer().fallDistance > 2.0F) {
            isFixed = true;
            mc.getTimer().timerSpeed = 0.9F;
        }

        if (mc.getPlayer().fallDistance > 2.9F) {
            PacketUtil.sendNoEvent(new C03PacketPlayer(true));
            mc.getPlayer().motionY = -0.1D;
            mc.getPlayer().fallDistance = 0;
            mc.getPlayer().motionY *= 1.1D;

            if (count++ > 5)
                count = 0;
        }
    }

    public String toString() {
        return "Vulcan";
    }
}
