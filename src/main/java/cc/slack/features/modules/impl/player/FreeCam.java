package cc.slack.features.modules.impl.player;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.MovementUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
        name = "FreeCam",
        category = Category.PLAYER
)
public class FreeCam extends Module {

    private EntityOtherPlayerMP entity;

    @Override
    public void onEnable() {
        entity = new EntityOtherPlayerMP(mc.getWorld(), mc.getPlayer().getGameProfile());
        entity.rotationYawHead = mc.getPlayer().rotationYawHead;
        entity.renderYawOffset = mc.getPlayer().renderYawOffset;
        entity.copyLocationAndAnglesFrom(mc.getPlayer());
        mc.getWorld().addEntityToWorld(-6969, entity);
    }

    @Override
    public void onDisable() {
        mc.getPlayer().setPosition(entity.posX, entity.posY, entity.posZ);
        mc.getWorld().removeEntity(entity);
    }

    @Listen
    public void onPacket (PacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            event.cancel();
        }
    }

    @Listen
    public void onMotion (MotionEvent event) {
        mc.getPlayer().motionY = 0;
        if(MovementUtil.isMoving())
            MovementUtil.setHClip(1);
        if(mc.getGameSettings().keyBindJump.isKeyDown()) MovementUtil.setVClip(0.5);
        if(mc.getGameSettings().keyBindSneak.isKeyDown()) MovementUtil.setVClip(-0.5);
    }

}
