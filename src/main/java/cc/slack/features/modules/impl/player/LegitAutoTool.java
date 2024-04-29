package cc.slack.features.modules.impl.player;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(
        name = "LegitAutoTool",
        category = Category.GHOST
)
public class LegitAutoTool extends Module {

    @SuppressWarnings("unused")
    private int prevItem = 0;
    private boolean isMining = false;
    private int bestSlot = 0;


    @Listen
    public void onRender (RenderEvent event) {
        if(event.getState() != RenderEvent.State.RENDER_3D) return;

        if (!mc.getGameSettings().keyBindUseItem.isKeyDown() && mc.getGameSettings().keyBindAttack.isKeyDown()
                && mc.getMinecraft().objectMouseOver != null
                && mc.getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {

            int bestSpeed = 0;
            bestSlot = -1;

            if (!isMining) {
                prevItem = mc.getPlayer().inventory.currentItem;
            }

            Block block = mc.getWorld().getBlockState(mc.getMinecraft().objectMouseOver.getBlockPos()).getBlock();

            for (int i = 0; i <= 8; i++) {
                ItemStack item = mc.getPlayer().inventory.getStackInSlot(i);
                if (item == null)
                    continue;

                float speed = item.getStrVsBlock(block);

                if (speed > bestSpeed) {
                    bestSpeed = (int) speed;
                    bestSlot = i;
                }

                if (bestSlot != -1) {
                    mc.getPlayer().inventory.currentItem = bestSlot;
                }
            }
            isMining = true;
        } else {
            if (isMining) {
                isMining = false;
            } else {
                prevItem = mc.getPlayer().inventory.currentItem;
            }
        }

    }

    @Listen
    public void onPacket (PacketEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity C02 = event.getPacket();
            if (C02.getAction() == C02PacketUseEntity.Action.ATTACK)
                isMining = false;
        }
    }

}
