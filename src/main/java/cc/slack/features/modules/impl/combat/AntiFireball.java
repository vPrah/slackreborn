// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.combat;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.render.RenderUtil;
import cc.slack.utils.rotations.RotationUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;

@ModuleInfo(
        name = "AntiFireball",
        category = Category.COMBAT
)
public class AntiFireball extends Module {

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate(UpdateEvent event) {
        for (Entity entity : mc.getWorld().loadedEntityList) {
            if (entity instanceof EntityFireball) {
                if (mc.thePlayer.getDistanceSqToEntity(entity) < 4) {
                    RotationUtil.setClientRotation(RotationUtil.getRotations(entity));
                    if (mc.thePlayer.getDistanceSqToEntity(entity) < 3.5) {
                        PacketUtil.send(new C0APacketAnimation());
                        PacketUtil.send(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                    }
                }
            }
        }
    }

    @Listen
    public void onRender (RenderEvent event) {
        if (event.getState() != RenderEvent.State.RENDER_3D) return;

        for(Entity entity : mc.getWorld().getLoadedEntityList()) {
            if (entity instanceof EntityFireball) {
                if (mc.thePlayer.getDistanceSqToEntity(entity) < 20) {
                    RenderUtil.drawTracer(entity, 255, (int) (mc.thePlayer.getDistanceSqToEntity(entity) * 10), 0, 170);
                }
            }
        }
    }
}
