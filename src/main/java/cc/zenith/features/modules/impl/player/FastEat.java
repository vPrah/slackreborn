package cc.zenith.features.modules.impl.player;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.network.PacketUtil;
import cc.zenith.utils.player.MovementUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
        name = "FastEat",
        category = Category.PLAYER
)
public class FastEat extends Module {

    double startY;

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().isUsingItem() && (mc.getPlayer().getItemInUse().getItem() instanceof ItemFood || mc.getPlayer().getItemInUse().getItem() instanceof ItemPotion)) {
            startY = mc.getPlayer().posY;
            MovementUtil.resetMotion(false);
            if(mc.getPlayer().onGround) {
                if(mc.getPlayer().posY <= startY) mc.getPlayer().setPosition(mc.getPlayer().posX, mc.getPlayer().posY - 0.00000001, mc.getPlayer().posZ);
            } else {
                if(mc.getPlayer().posY <= startY) PacketUtil.sendNoEvent(new C03PacketPlayer(false), 3);
            }
        }
    }

}
