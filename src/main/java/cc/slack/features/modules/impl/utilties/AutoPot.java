package cc.slack.features.modules.impl.utilties;

import cc.slack.events.State;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;

@ModuleInfo(
        name = "AutoPot",
        category = Category.UTILITIES
)
public class AutoPot extends Module {

    private final NumberValue<Double> healthValue = new NumberValue<>("Health", 10.0D, 1.0D, 20.0D, 0.5D);
    private final NumberValue<Integer> delayValue = new NumberValue<>("Delay", 100, 50, 1000, 50);

    


}
