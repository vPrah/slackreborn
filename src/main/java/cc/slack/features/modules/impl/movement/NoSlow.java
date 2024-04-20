package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

@ModuleInfo(
        name = "NoSlow",
        category = Category.MOVEMENT
)

public class NoSlow extends Module {

    private final ModeValue<String> mode = new ModeValue<>("Mode", new String[]{"Vanilla", "NCPLatest", "Switch"});

    public final NumberValue<Float> strafeMultiplier = new NumberValue<>("MoveStrafeMultiplier", 1f, 0.2f,1f, 0.10f);
    public final NumberValue<Float> forwardMultiplier = new NumberValue<>("ForwardMultiplier", 1f, 0.2f,1f, 0.10f);

    public NoSlow() {
        addSettings(mode, strafeMultiplier, forwardMultiplier);
    }

    @Listen
    public void onUpdate (UpdateEvent event) {
        if (mc.getPlayer().isUsingItem() && (mc.getPlayer().getHeldItem().item instanceof ItemFood)) {
            switch (mode.getValue().toLowerCase()) {
                case "vanilla":
                    break;
                case "ncplatest":
                case "switch":
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.getPlayer().inventory.currentItem % 8 + 1));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.getPlayer().inventory.currentItem));
                    break;
            }
        }
    }

}
