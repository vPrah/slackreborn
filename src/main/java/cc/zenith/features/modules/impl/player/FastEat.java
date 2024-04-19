package cc.zenith.features.modules.impl.player;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.utils.client.MC;
import cc.zenith.utils.network.PacketUtil;
import cc.zenith.utils.player.MoveUtil;
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
        if (MC.getPlayer().isUsingItem() && (MC.getPlayer().getItemInUse().getItem() instanceof ItemFood || MC.getPlayer().getItemInUse().getItem() instanceof ItemPotion)) {
            startY = MC.getPlayer().posY;
            MoveUtil.resetMotion(false);
            if(MC.getPlayer().onGround) {
                if(MC.getPlayer().posY <= startY) MC.getPlayer().setPosition(MC.getPlayer().posX, MC.getPlayer().posY - 0.00000001, MC.getPlayer().posZ);
            } else {
                if(MC.getPlayer().posY <= startY) PacketUtil.sendNoEvent(new C03PacketPlayer(false), 3);
            }
        }
    }

}
