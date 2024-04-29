// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.combat;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.player.RotationUtil;
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

    @Listen
    public void onUpdate(UpdateEvent event) {
        for (Entity entity : mc.getWorld().loadedEntityList) {
            if (entity instanceof EntityFireball) {
                if (mc.getPlayer().getDistanceSqToEntity(entity) < 3) {
                    RotationUtil.setClientRotation(RotationUtil.getRotations(entity));
                    PacketUtil.send(new C0APacketAnimation());
                    PacketUtil.send(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                }
            }
        }
    }
}
