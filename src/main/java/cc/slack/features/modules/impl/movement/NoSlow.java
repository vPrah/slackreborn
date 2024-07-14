// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.Slack;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.features.modules.impl.combat.KillAura;
import cc.slack.utils.network.PacketUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;

@ModuleInfo(
        name = "NoSlow",
        category = Category.MOVEMENT
)

public class NoSlow extends Module {

    // Slow Modes
    public final ModeValue<String> blockmode = new ModeValue<>("Block", new String[]{"None", "Vanilla", "NCP Latest", "Hypixel", "Hypixel Spoof", "Switch", "Place", "C08 Tick"});
    public final ModeValue<String> eatmode = new ModeValue<>("Eat", new String[]{"None","Vanilla", "NCP Latest", "Hypixel", "Switch", "Place", "C08 Tick"});
    public final ModeValue<String> potionmode = new ModeValue<>("Potion", new String[]{"None","Vanilla", "NCP Latest", "Hypixel", "Switch", "Place", "C08 Tick"});
    public final ModeValue<String> bowmode = new ModeValue<>("Bow", new String[]{"None","Vanilla", "NCP Latest", "Hypixel", "Switch", "Place", "C08 Tick"});


    public final NumberValue<Float> forwardMultiplier = new NumberValue<>("Forward Multiplier", 1f, 0.2f,1f, 0.05f);
    public final NumberValue<Float> strafeMultiplier = new NumberValue<>("Strafe Multiplier", 1f, 0.2f,1f, 0.05f);


    public float fMultiplier = 0F;
    public float sMultiplier = 0F;
    public boolean sprinting = true;

    public NoSlow() {
        addSettings(blockmode, eatmode, potionmode, bowmode
                , forwardMultiplier, strafeMultiplier);
    }

    private boolean badC07 = false;


    @SuppressWarnings("unused")
    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer == null || mc.thePlayer.getHeldItem() == null || mc.thePlayer.getHeldItem().item == null) return;

        fMultiplier = forwardMultiplier.getValue();
        sMultiplier = strafeMultiplier.getValue();

        boolean usingItem = mc.thePlayer.isUsingItem() || (Slack.getInstance().getModuleManager().getInstance(KillAura.class).isToggle() && Slack.getInstance().getModuleManager().getInstance(KillAura.class).isBlocking);

        if (usingItem && mc.thePlayer.getHeldItem().item instanceof ItemSword) {
            String mode = blockmode.getValue().toLowerCase();
            processModeSword(mode);
        }

        if (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().item instanceof ItemFood) {
            String mode = eatmode.getValue().toLowerCase();
            processModeEat(mode);
        }

        if (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().item instanceof ItemPotion) {
            String mode = potionmode.getValue().toLowerCase();
            processModePotion(mode);
        }

        if (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().item instanceof ItemBow) {
            String mode = bowmode.getValue().toLowerCase();
            processModeBow(mode);
        }

        badC07 = false;
    }

    // onSword (Blocking)
    private void processModeSword(String mode) {
        switch (mode) {
            case "none":
                setMultipliers(0.2F, 0.2F);
                break;
            case "vanilla":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                break;
            case "ncp latest":
            case "switch":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                break;
            case "place":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                break;
            case "c08 tick":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                if (mc.thePlayer.ticksExisted % 3 == 0) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                }
                break;
            case "hypixel spoof":
                setMultipliers(1, 1);
                if (mc.thePlayer.isSprinting()) {
                    switch (mc.thePlayer.ticksExisted % 4) {
                        case 0:
                            PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                            break;
                        case 1:
                            PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                            break;
                    }
                }
                break;
            case "hypixel":
                setMultipliers(1, 1);
                if (mc.thePlayer.isSprinting()) {
                    switch (mc.thePlayer.ticksExisted % 4) {
                        case 0:
                            sprinting = false;
                            break;
                        case 1:
                            sprinting = true;
                            break;
                    }
                }
                break;
        }
    }


    // onEat (Eating and drinking)
    private void processModeEat(String mode) {
        switch (mode) {
            case "none":
                setMultipliers(0.2F, 0.2F);
                break;
            case "vanilla":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                break;
            case "ncp latest":
            case "switch":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                break;
            case "place":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                break;
            case "c08 tick":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                if (mc.thePlayer.ticksExisted % 3 == 0) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                }
                break;
            case "hypixel":
                if (mc.thePlayer.ticksExisted % 3 == 0 && !badC07 && !(mc.thePlayer.getHeldItem().item instanceof ItemSword)) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 1, null, 0.0f, 0.0f, 0.0f));
                }
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                break;
        }
    }

    // onPotion Slow
    private void processModePotion(String mode) {
        switch (mode) {
            case "none":
                setMultipliers(0.2F, 0.2F);
                break;
            case "vanilla":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                break;
            case "ncp latest":
            case "switch":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                break;
            case "place":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                break;
            case "c08 tick":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                if (mc.thePlayer.ticksExisted % 3 == 0) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                }
                break;
            case "hypixel":
                if (mc.thePlayer.ticksExisted % 3 == 0 && !badC07) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 1, null, 0.0f, 0.0f, 0.0f));
                }
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                break;
        }
    }

    // onBow Slow
    private void processModeBow(String mode) {
        switch (mode) {
            case "none":
                setMultipliers(0.2F, 0.2F);
                break;
            case "vanilla":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                break;
            case "ncp latest":
            case "switch":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                break;
            case "place":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                break;
            case "c08 tick":
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                if (mc.thePlayer.ticksExisted % 3 == 0) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                }
                break;
            case "hypixel":
                if (mc.thePlayer.ticksExisted % 3 == 0 && !badC07 && !(mc.thePlayer.getHeldItem().item instanceof ItemSword)) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 1, null, 0.0f, 0.0f, 0.0f));
                }
                setMultipliers(forwardMultiplier.getValue(), strafeMultiplier.getValue());
                break;
        }
    }

    private void setMultipliers(float forward, float strafe) {
        fMultiplier = forward;
        sMultiplier = strafe;
        sprinting = true;
    }

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.getPacket() instanceof C07PacketPlayerDigging) badC07 = true;
    }

    @Override
    public String getMode() { return blockmode.getValue() + ", "  + eatmode.getValue(); }

}
