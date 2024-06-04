// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.player;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.player.MovementUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
        name = "FastEat",
        category = Category.PLAYER
)
public class FastEat extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Instant", "Clip"});

    public FastEat(){
        addSettings(mode);
    }

    double startY;

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().isUsingItem() && (mc.getPlayer().getItemInUse().getItem() instanceof ItemFood || mc.getPlayer().getItemInUse().getItem() instanceof ItemPotion || mc.getPlayer().getItemInUse().getItem() instanceof ItemBucketMilk)) {
            switch (mode.getValue()) {
                case "Instant":
                    PacketUtil.sendNoEvent(new C03PacketPlayer(mc.getPlayer().onGround), 30);
                    break;
                case "Clip":
                startY = mc.getPlayer().posY;
                MovementUtil.resetMotion(false);
                if (mc.getPlayer().onGround) {
                    if (mc.getPlayer().posY <= startY)
                        mc.getPlayer().setPosition(mc.getPlayer().posX, mc.getPlayer().posY - 0.00000001, mc.getPlayer().posZ);
                } else {
                    if (mc.getPlayer().posY <= startY) PacketUtil.sendNoEvent(new C03PacketPlayer(false), 3);
                }
                break;
            }
        }
    }

}
