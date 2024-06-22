// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.Slack;
import cc.slack.events.State;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.features.modules.impl.combat.KillAura;
import cc.slack.utils.network.PacketUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(
        name = "NoSlow",
        category = Category.MOVEMENT
)

public class NoSlow extends Module {

    public final ModeValue<String> mode = new ModeValue<>("Bypass", new String[]{"Vanilla", "NCP Latest", "Hypixel", "Switch", "Place", "C08 Tick"});

    public final NumberValue<Float> forwardMultiplier = new NumberValue<>("Forward Multiplier", 1f, 0.2f,1f, 0.05f);
    public final NumberValue<Float> strafeMultiplier = new NumberValue<>("Strafe Multiplier", 1f, 0.2f,1f, 0.05f);

    public float fMultiplier = 0F;
    public float sMultiplier = 0F;

    public final BooleanValue onEat = new BooleanValue("Eating NoSlow", true);

    public NoSlow() {
        addSettings(mode, forwardMultiplier, strafeMultiplier, onEat);
    }

    private boolean badC07 = false;


    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {
        if (mc.thePlayer == null) return;
        if (mc.thePlayer.getHeldItem() == null) return;
        if (mc.thePlayer.getHeldItem().item == null) return;

        fMultiplier = forwardMultiplier.getValue();
        sMultiplier = strafeMultiplier.getValue();


        boolean usingItem = mc.thePlayer.isUsingItem() || (Slack.getInstance().getModuleManager().getInstance(KillAura.class).isToggle() && Slack.getInstance().getModuleManager().getInstance(KillAura.class).isBlocking);

        if (usingItem && (mc.thePlayer.getHeldItem().item instanceof ItemSword) || (mc.thePlayer.isUsingItem() && ((mc.thePlayer.getHeldItem().item instanceof ItemFood || mc.thePlayer.getHeldItem().item instanceof ItemPotion) && onEat.getValue()))) {
            switch (mode.getValue().toLowerCase()) {
                case "vanilla":
                    fMultiplier = forwardMultiplier.getValue();
                    sMultiplier = strafeMultiplier.getValue();
                    break;
                case "ncp latest":
                case "switch":
                    fMultiplier = forwardMultiplier.getValue();
                    sMultiplier = strafeMultiplier.getValue();
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    break;
                case "place":
                    fMultiplier = forwardMultiplier.getValue();
                    sMultiplier = strafeMultiplier.getValue();
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    break;
                case "c08 tick":
                    fMultiplier = forwardMultiplier.getValue();
                    sMultiplier = strafeMultiplier.getValue();
                    if (mc.thePlayer.ticksExisted % 3 == 0) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    }
                    break;
                case "hypixel":
                    if (mc.thePlayer.ticksExisted % 3 == 0 && !badC07) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 1, null, 0.0f, 0.0f, 0.0f));
                    }
                    fMultiplier = forwardMultiplier.getValue();
                    sMultiplier = strafeMultiplier.getValue();
                    break;

            }
        }
        badC07 = false;
    }

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.getPacket() instanceof C07PacketPlayerDigging) badC07 = true;
    }

    @Override
    public String getMode() { return mode.getValue(); }

}
