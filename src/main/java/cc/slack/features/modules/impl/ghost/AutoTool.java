// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.TimeUtil;
import cc.slack.utils.player.AttackUtil;
import cc.slack.utils.player.ItemSpoofUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(
        name = "AutoTool",
        category = Category.GHOST
)
public class AutoTool extends Module {

    private final BooleanValue noCombat = new BooleanValue("No Combat", true);
    private final NumberValue<Integer> delay = new NumberValue<>("Switch Delay", 90, 0, 500, 10);
    private final BooleanValue spoof = new BooleanValue("Spoof Item", false);

    public AutoTool(){
        addSettings(noCombat, delay, spoof);
    }

    private final TimeUtil switchTimer = new TimeUtil();

    @SuppressWarnings("unused")
    private int prevItem = 0;
    private boolean isMining = false;
    private int bestSlot = 0;


    @Listen
    public void onRender (RenderEvent event) {
        if(event.getState() != RenderEvent.State.RENDER_3D) return;
        if (!mc.getGameSettings().keyBindUseItem.isKeyDown() && mc.getGameSettings().keyBindAttack.isKeyDown()
                && mc.getMinecraft().objectMouseOver != null
                && mc.getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                && !(AttackUtil.inCombat && noCombat.getValue())) {
            getTool(true, mc.getWorld().getBlockState(mc.getMinecraft().objectMouseOver.getBlockPos()).getBlock(), delay.getValue(), spoof.getValue());
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

    public void getTool (Boolean enabled, Block block, Integer delay, Boolean spoof) {
        if (enabled) {
            if (switchTimer.hasReached(delay)) {

                bestSlot = -1;

                if (!isMining) {
                    prevItem = mc.getPlayer().inventory.currentItem;
                }

                float bestSpeed = mc.getPlayer().inventory.getStackInSlot(mc.getPlayer().inventory.currentItem).getStrVsBlock(block);
                for (int i = 0; i <= 8; i++) {
                    ItemStack item = mc.getPlayer().inventory.getStackInSlot(i);
                    if (item == null)
                        continue;

                    float speed = item.getStrVsBlock(block);

                    if (speed > bestSpeed) {
                        bestSpeed = speed;
                        bestSlot = i;
                    }

                    if (bestSlot != -1) {
                        if (spoof) {
                            ItemSpoofUtil.startSpoofing(bestSlot);
                        } else {
                            mc.getPlayer().inventory.currentItem = bestSlot;
                        }
                    }
                }
                isMining = true;
            }
        } else {
            switchTimer.reset();
            if (isMining) {
                isMining = false;
                if (spoof) {
                    ItemSpoofUtil.stopSpoofing();
                }
                mc.getPlayer().inventory.currentItem = prevItem;
            } else {
                prevItem = mc.getPlayer().inventory.currentItem;
            }
        }
    }

}
