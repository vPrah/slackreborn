// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.utilties;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.BlockUtils;
import cc.slack.utils.other.TimeUtil;
import cc.slack.utils.player.ItemSpoofUtil;
import cc.slack.utils.player.PlayerUtil;
import io.github.nevalackin.radbus.Listen;


@ModuleInfo(
        name = "AutoTool",
        category = Category.UTILITIES
)
public class AutoTool extends Module {

    private final NumberValue<Integer> delay = new NumberValue<>("Switch Delay", 100, 0, 600, 20);

    public AutoTool(){
        addSettings(delay);
    }

    private final TimeUtil switchTimer = new TimeUtil();
    private boolean isSpoofing = false;

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayerController().isHittingBlock) {
            if (switchTimer.hasReached((long) delay.getValue())) {
                int bestTool = PlayerUtil.getBestHotbarTool(BlockUtils.getBlock(mc.getMinecraft().objectMouseOver.getBlockPos()));
                if (bestTool != mc.getPlayer().inventory.currentItem) {
                    ItemSpoofUtil.startSpoofing(bestTool);
                    isSpoofing = true;
                }
            }
        } else {
            switchTimer.reset();
            if (isSpoofing) {
                isSpoofing = false;
                ItemSpoofUtil.stopSpoofing();
            }
        }
    }
}
