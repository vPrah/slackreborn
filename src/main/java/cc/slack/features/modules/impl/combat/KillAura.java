// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.combat;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.features.modules.impl.movement.Flight;
import cc.slack.features.modules.impl.world.Scaffold;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.other.MathUtil;
import cc.slack.utils.player.InventoryUtil;
import cc.slack.utils.render.RenderUtil;
import cc.slack.utils.rotations.RaycastUtil;
import cc.slack.utils.other.TimeUtil;
import cc.slack.utils.player.AttackUtil;
import cc.slack.utils.player.BlinkUtil;
import cc.slack.utils.rotations.RotationUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;


import java.security.SecureRandom;

@ModuleInfo(
        name = "KillAura",
        category = Category.COMBAT,
        key = Keyboard.KEY_R
)
public class KillAura extends Module {

    // range
    private final NumberValue<Double> aimRange = new NumberValue<>("Aim Range", 7.0D, 3.0D, 12.0D, 0.01D);
    public final NumberValue<Double> attackRange = new NumberValue<>("Attack Range", 3.0D, 3.0D, 7.0D, 0.01D);

    // attack
    private final ModeValue<String> swingMode = new ModeValue<>("Swing", new String[]{"Normal", "Packet", "None"});
    private final ModeValue<AttackUtil.AttackPattern> attackPattern = new ModeValue<>("Pattern", AttackUtil.AttackPattern.values());
    private final NumberValue<Integer> cps = new NumberValue<>("CPS", 14, 1, 30, 1);
    private final NumberValue<Double> randomization = new NumberValue<>("Randomization", 1.50D, 0D, 4D, 0.01D);

    // autoblock
    private final ModeValue<String> autoBlock = new ModeValue<>("Autoblock", new String[]{"None", "Fake", "Blatant", "Vanilla", "Basic", "Interact", "Blink", "Switch", "Hypixel", "Vanilla Reblock", "Double Sword", "Legit"});
    private final ModeValue<String> blinkMode = new ModeValue<>("Blink Autoblock Mode", new String[]{"Legit", "Legit HVH", "Blatant"});
    private final NumberValue<Double> blockRange = new NumberValue<>("Block Range", 3.0D, 0.0D, 7.0D, 0.01D);
    private final BooleanValue interactAutoblock = new BooleanValue("Interact", false);
    private final BooleanValue renderBlocking = new BooleanValue("Render Blocking", true);

    // rotation
    private final ModeValue<RotationUtil.TargetRotation> rotationMode = new ModeValue<>("Rotation Mode", RotationUtil.TargetRotation.values());
    private final BooleanValue rotationRand = new BooleanValue("Rotation Randomization", false);
    private final NumberValue<Double> minRotationSpeed = new NumberValue<>("Min Rotation Speed", 65.0, 0.0, 180.0, 5.0);
    private final NumberValue<Double> maxRotationSpeed = new NumberValue<>("Max Rotation Speed", 85.0, 0.0, 180.0, 5.0);
    private final BooleanValue checkHitable = new BooleanValue("Check Hitable", false);


    // tools
    private final BooleanValue moveFix = new BooleanValue("Move Fix", false);
    private final BooleanValue keepSprint = new BooleanValue("Keep Sprint", true);
    private final BooleanValue rayCast = new BooleanValue("Ray Cast", true);

    // Checks
    private final BooleanValue noScaffold = new BooleanValue("Disable on Scaffold", false);
    private final BooleanValue noFlight = new BooleanValue("Disable on Flight", false);
    private final BooleanValue noEat = new BooleanValue("Disable on Eat", true);
    private final BooleanValue noBlock = new BooleanValue("Disable on Block", true);

    private final ModeValue<String> sortMode = new ModeValue<>("Sort", new String[]{"Priority", "FOV", "Distance", "Health", "Hurt Ticks"});
    private final ModeValue<String> markMode = new ModeValue<>("Killaura Mark Mode", new String[]{"None", "Tracer"});


    private final TimeUtil timer = new TimeUtil();
    private final TimeUtil rotationCenter = new TimeUtil();
    private double rotationOffset;
    public EntityLivingBase target;
    private EntityLivingBase rayCastedEntity;
    private float[] rotations;
    private long attackDelay;
    private int queuedAttacks;
    public boolean isBlocking;
    private boolean wasBlink;
    private boolean inInv = false;

    private boolean hasDouble = false;
    private int currentSlot = 0;
    private int sword1 = 0;
    private int sword2 = 0;

    public boolean renderBlock;

    public KillAura() {
        super();
        addSettings(
                aimRange, attackRange, // range
                swingMode, attackPattern, cps, randomization, // autoclicker
                autoBlock, blinkMode, blockRange, interactAutoblock, renderBlocking, // autoblock
                rotationMode, rotationRand, minRotationSpeed, maxRotationSpeed, checkHitable, // rotations
                moveFix, keepSprint, rayCast, // fixes
                noScaffold, noFlight, noEat, noBlock, // Checks
                sortMode,
                markMode);
    }

    @Override
    public void onEnable() {
        wasBlink = false;
        rotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        attackDelay = AttackUtil.getAttackDelay(cps.getValue(), randomization.getValue(), attackPattern.getValue());
        queuedAttacks = 0;
        timer.reset();
        rotationCenter.reset();
        currentSlot = mc.thePlayer.inventory.currentItem;
        if (mc.currentScreen instanceof GuiInventory) inInv = true;
    }

    @Override
    public void onDisable() {
        target = null;
        if(isBlocking) unblock();
        if(wasBlink) BlinkUtil.disable();
        if (inInv) PacketUtil.send(new C0DPacketCloseWindow());
        if (!hasDouble && currentSlot != mc.thePlayer.inventory.currentItem && autoBlock.getValue().equalsIgnoreCase("double sword")) {
            PacketUtil.send(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            currentSlot = mc.thePlayer.inventory.currentItem;
        }
    }

    @Listen
    public void onRender(RenderEvent e) {
        if(e.getState() != RenderEvent.State.RENDER_3D) return;
        if (timer.hasReached(attackDelay) && target != null) {
            queuedAttacks++;
            timer.reset();
            attackDelay = AttackUtil.getAttackDelay(cps.getValue(), randomization.getValue(), attackPattern.getValue());
        }

        if (target != null) {
            switch (markMode.getValue().toLowerCase()) {
                case "tracer":
                    RenderUtil.drawTracer(target, 250, 250, 250, 130);
                    break;
                default:
                    break;
            }
        }
    }

    @Listen
    public void onUpdate(UpdateEvent e) {

        if (noBlock.getValue() && mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().item instanceof ItemBlock) return;
        if (noScaffold.getValue() && Slack.getInstance().getModuleManager().getInstance(Scaffold.class).isToggle()) return;
        if (noEat.getValue() && mc.thePlayer.isUsingItem() && (mc.thePlayer.getHeldItem().item instanceof ItemFood || mc.thePlayer.getHeldItem().item instanceof ItemBucketMilk || mc.thePlayer.isUsingItem() && (mc.thePlayer.getHeldItem().item instanceof ItemPotion))) return;
        if (noFlight.getValue() && Slack.getInstance().getModuleManager().getInstance(Flight.class).isToggle()) return;

        target = AttackUtil.getTarget(aimRange.getValue(), sortMode.getValue());

        if (target == null) {
            attackDelay = 0;
            if (isBlocking)
                unblock();
            if (wasBlink) {
                wasBlink = false;
                BlinkUtil.disable();
            }
            renderBlock = false;
            rotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
            return;
        }

        if (mc.thePlayer.getDistanceToEntity(target) > aimRange.getValue()) return;
        renderBlock = canAutoBlock() && (renderBlocking.getValue() || isBlocking || autoBlock.getValue().equals("Fake")) && (!autoBlock.getValue().equals("None"));

        rotations = calculateRotations(target);

        RotationUtil.setClientRotation(rotations, 1);
        RotationUtil.setStrafeFix(moveFix.getValue(), false);

        sword1 = -1;
        sword2 = -1;
        for (int i = 36; i < 45; i++) {
            if (InventoryUtil.getSlot(i).getHasStack()) {
                ItemStack itemStack = InventoryUtil.getSlot(i).getStack();
                if (itemStack.item instanceof ItemSword) {
                    if (sword1 == -1) {
                        sword1 = i - 36;
                    } else {
                        sword2 = i - 36;
                    }
                }
            }
        }

        hasDouble = sword2 != -1;

        if (preTickBlock()) return;
        
        if (queuedAttacks == 0) return;
        
        if (isBlocking)
            if (preAttack()) return;

        while (queuedAttacks > 0) {
            attack(target);
            queuedAttacks--;
        }
        if (canAutoBlock()) postAttack();
    }

    private void attack(EntityLivingBase target) {
        rayCastedEntity = null;
        if (rayCast.getValue()) rayCastedEntity = RaycastUtil.rayCast(attackRange.getValue(), rotations);

        if (swingMode.getValue().contains("Normal")){
            mc.thePlayer.swingItem();
        } else if (swingMode.getValue().contains("Packet")) {
            PacketUtil.sendNoEvent(new C0APacketAnimation());
        }


        if (mc.thePlayer.getDistanceToEntity(rayCastedEntity == null ? target : rayCastedEntity) > attackRange.getValue() + 0.3)
            return;

        if (checkHitable.getValue() && !RaycastUtil.itHitable(attackRange.getValue() + 0.5, rotations, target)) return;

        if (keepSprint.getValue()) {
            mc.getPlayerController().syncCurrentPlayItem();
            PacketUtil.send(new C02PacketUseEntity(rayCastedEntity == null ? target : rayCastedEntity, C02PacketUseEntity.Action.ATTACK));
            if (mc.thePlayer.fallDistance > 0 && !mc.thePlayer.onGround) {
                mc.thePlayer.onCriticalHit(rayCastedEntity == null ? target : rayCastedEntity);
            }
        } else {
            mc.getPlayerController().attackEntity(mc.thePlayer, rayCastedEntity == null ? target : rayCastedEntity);
        }
    }

    private boolean preTickBlock() {
        switch (autoBlock.getValue().toLowerCase()) {
            case "double sword":
                if (!hasDouble && currentSlot != mc.thePlayer.inventory.currentItem && !isBlocking && target == null) {
                    PacketUtil.send(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    currentSlot = mc.thePlayer.inventory.currentItem;
                }
                if (hasDouble && currentSlot != mc.thePlayer.inventory.currentItem) {
                    PacketUtil.send(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    currentSlot = mc.thePlayer.inventory.currentItem;
                }
                break;
            case "legit":
                if (mc.thePlayer.hurtTime < 3) {
                    if (!isBlocking) {
                        block(false);
                    }
                } else {
                    if (isBlocking) unblock();
                }
                return isBlocking;
            case "basic":
                switch (mc.thePlayer.ticksExisted % 3) {
                    case 0:
                        unblock();
                        return true;
                    case 1:
                        return false;
                    case 2:
                        block();
                        return true;
                }
                break;
            case "blink":
                switch (blinkMode.getValue().toLowerCase()) {
                    case "legit":
                        switch (mc.thePlayer.ticksExisted % 3) {
                            case 0:
                                unblock();
                                return true;
                            case 1:
                                return false;
                            case 2:
                                block();
                                if (!BlinkUtil.isEnabled)
                                    BlinkUtil.enable(false, true);
                                BlinkUtil.setConfig(false, true);
                                BlinkUtil.releasePackets();
                                wasBlink = true;
                                return true;
                        }
                        break;
                    case "legit hvh":
                        switch (mc.thePlayer.ticksExisted % 5) {
                            case 0:
                                unblock();
                                return true;
                            case 4:
                                block();
                                if (!BlinkUtil.isEnabled)
                                    BlinkUtil.enable(true, true);
                                BlinkUtil.setConfig(true, true);
                                BlinkUtil.releasePackets();
                                wasBlink = true;
                                return true;
                        }
                        break;
                    case "blatant":
                        switch (mc.thePlayer.ticksExisted % 2) {
                            case 0:
                                unblock();
                                return true;
                            case 1:
                                return false;
                        }
                        break;
                }
                break;
            default:
                break;
        }
        return false;
    }

    private boolean preAttack() {
        switch (autoBlock.getValue().toLowerCase()) {
            case "double sword":
                if (hasDouble) {
                    if (currentSlot != sword1) {
                        PacketUtil.send(new C09PacketHeldItemChange(sword1));
                        currentSlot = sword1;
                    } else {
                        PacketUtil.send(new C09PacketHeldItemChange(sword2));
                        currentSlot = sword2;
                    }
                }
                break;
            case "blatant":
                unblock();
                isBlocking = false;
                break;
            case "drop":
                if (isBlocking) {
                    PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                }
                isBlocking = false;
                break;
            case "switch":
                if (isBlocking) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
                isBlocking = false;
                break;
            case "test":
                if (isBlocking) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    BlinkUtil.disable();
                    isBlocking = false;
                    return false;
                }
                break;
            case "hypixel":
                if (isBlocking) {
                    PacketUtil.send(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 1, null, 0.0f, 0.0f, 0.0f));
                    isBlocking = false;
                    return false;
                }
            default:
                break;
        }
        return false;
    }

    private void postAttack() {
        switch (autoBlock.getValue().toLowerCase()) {
            case "interact":
                if (mc.thePlayer.hurtTime < 4)
                    block(true);
                break;
            case "hypixel":
                if (mc.thePlayer.hurtTime < 4 && target.hurtTime > 1)
                    block(true);
                break;
            case "vanilla reblock":
                isBlocking = false;
                block();
                break;
            case "test":
                block();
                BlinkUtil.enable(false, true);
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                break;
            case "double sword":
                if (hasDouble) {
                    block();
                }
                break;
            case "drop":
            case "switch":
            case "blatant":
            case "vanilla":
                block();
                break;
            case "blink":
                if (blinkMode.getValue().equals("Blatant")) {
                    block();
                    if (!BlinkUtil.isEnabled)
                        BlinkUtil.enable(false, true);
                    BlinkUtil.setConfig(false, true);
                    BlinkUtil.releasePackets();
                    wasBlink = true;
                }
                break;
            default:
                break;
        }
    }

    private float[] calculateRotations(Entity entity) {
        final AxisAlignedBB bb = entity.getEntityBoundingBox();

        if(rotationCenter.hasReached(1200) && rotationRand.getValue()) {
            rotationOffset = new SecureRandom().nextDouble() / 4;
            rotationCenter.reset();
        }

        float[] newRots = RotationUtil.getLimitedRotation(
                rotations,
                RotationUtil.getTargetRotations(bb, rotationMode.getValue(), rotationOffset),
                (float) MathUtil.getRandomInRange(minRotationSpeed.getValue(), maxRotationSpeed.getValue()));

        return RotationUtil.applyGCD(newRots, rotations);
    }

    private void block() {
        block(interactAutoblock.getValue());
    }

    private void block(boolean interact) {
        if (isBlocking) return;
        EntityLivingBase targetEntity = rayCastedEntity == null ? target : rayCastedEntity;
        if (interact) {
            PacketUtil.send(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.INTERACT));
            // PacketUtil.send(new C02PacketUseEntity(targetEntity, new Vec3(0,0,0)));
        }
        PacketUtil.sendBlocking(true, false);
        isBlocking = true;
    }

    private void unblock() {
        if (!isBlocking) return;
        if (!mc.getGameSettings().keyBindUseItem.isKeyDown())
            PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        else
            if (canAutoBlock()) mc.getGameSettings().keyBindUseItem.setPressed(false);
        isBlocking = false;
    }

    private boolean canAutoBlock() {
        return target != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.getDistanceToEntity(target) < blockRange.getValue();
    }

    @Override
    public String getMode() { return cps.getValue() + " cps" + ", " + autoBlock.getValue(); }
}
