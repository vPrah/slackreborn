// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.features.modules.impl.combat.KillAura;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

@ModuleInfo(
        name = "NoSlow",
        category = Category.MOVEMENT
)

public class NoSlow extends Module {

    public final ModeValue<String> mode = new ModeValue<>(new String[]{"Vanilla", "Vulcan", "NCP Latest", "Switch", "Place", "C08 Tick"});

    public final NumberValue<Float> forwardMultiplier = new NumberValue<>("Forward Multiplier", 1f, 0.2f,1f, 0.05f);
    public final NumberValue<Float> strafeMultiplier = new NumberValue<>("Strafe Multiplier", 1f, 0.2f,1f, 0.05f);

    public NoSlow() {
        super();
        addSettings(mode, forwardMultiplier, strafeMultiplier);
    }

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {
        boolean usingItem = mc.getPlayer().isUsingItem() || (Slack.getInstance().getModuleManager().getInstance(KillAura.class).isToggle() || Slack.getInstance().getModuleManager().getInstance(KillAura.class).isBlocking);

        if (usingItem && (mc.getPlayer().getHeldItem().item instanceof ItemSword) || mc.getPlayer().isUsingItem() && (mc.getPlayer().getHeldItem().item instanceof ItemFood)) {
            switch (mode.getValue().toLowerCase()) {
                case "vulcan":
                case "vanilla":
                    break;
                case "ncp latest":
                case "switch":
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.getPlayer().inventory.currentItem % 8 + 1));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.getPlayer().inventory.currentItem));
                    break;
                case "place":
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.getPlayer().inventory.getCurrentItem()));
                    break;
                case "c08 tick":
                    if (mc.getPlayer().ticksExisted % 3 == 0) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.getPlayer().getHeldItem()));
                    }
                    break;
            }
        }
    }

}
