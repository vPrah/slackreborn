package cc.slack.features.modules.impl.movement.spiders.impl;

import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.impl.movement.spiders.ISpider;
import cc.slack.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;

public class CollideSpider implements ISpider {

    @Override
    public void onMotion(MotionEvent event) {
        if (!mc.thePlayer.onGround && !mc.gameSettings.keyBindSneak.isKeyDown()) {
            if (mc.thePlayer.posY + mc.thePlayer.motionY < Math.floor(mc.thePlayer.posY))
                if (!(PlayerUtil.getBlockRelativeToPlayer(-1, -1, 0) instanceof BlockAir) || !(PlayerUtil.getBlockRelativeToPlayer(1, -1, 0) instanceof BlockAir) || !(PlayerUtil.getBlockRelativeToPlayer(0, -1, -1) instanceof BlockAir) || !(PlayerUtil.getBlockRelativeToPlayer(0, -1, 1) instanceof BlockAir))
                    mc.thePlayer.motionY = (Math.floor(mc.thePlayer.posY) - (mc.thePlayer.posY));
            if (mc.thePlayer.motionY == 0) {
                mc.thePlayer.onGround = true;
                event.setGround(true);
            }
        }
    }

    @Override
    public String toString() {
        return "Collide";
    }
}
