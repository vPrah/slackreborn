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
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.other.MathUtil;
import cc.slack.utils.other.RaycastUtil;
import cc.slack.utils.other.TimeUtil;
import cc.slack.utils.player.AttackUtil;
import cc.slack.utils.player.BlinkUtil;
import cc.slack.utils.rotations.RotationUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
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
    private final NumberValue<Double> attackRange = new NumberValue<>("Attack Range", 3.0D, 3.0D, 7.0D, 0.01D);

    // attack
    private final ModeValue<AttackUtil.AttackPattern> attackPattern = new ModeValue<>("Pattern", AttackUtil.AttackPattern.values());
    private final NumberValue<Integer> cps = new NumberValue<>("CPS", 14, 1, 30, 1);
    private final NumberValue<Double> randomization = new NumberValue<>("Randomization", 1.50D, 0D, 4D, 0.01D);

    // autoblock
    private final ModeValue<String> autoBlock = new ModeValue<>("Autoblock", new String[]{"None", "Fake", "Blatant", "Vanilla", "Basic", "Interact", "Blink"});
    private final ModeValue<String> blinkMode = new ModeValue<>("Blink Autoblock Mode", new String[]{"Legit", "Legit HVH", "Blatant"});
    private final NumberValue<Double> blockRange = new NumberValue<>("Block Range", 3.0D, 0.0D, 7.0D, 0.01D);
    private final BooleanValue interactAutoblock = new BooleanValue("Interact", false);
    private final BooleanValue renderBlocking = new BooleanValue("Render Blocking", true);

    // rotation
    private final BooleanValue rotationRand = new BooleanValue("Rotation Randomization", false);
    private final NumberValue<Double> minRotationSpeed = new NumberValue<>("Min Rotation Speed", 65.0, 0.0, 180.0, 5.0);
    private final NumberValue<Double> maxRotationSpeed = new NumberValue<>("Max Rotation Speed", 85.0, 0.0, 180.0, 5.0);


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


    private final TimeUtil timer = new TimeUtil();
    private final TimeUtil rotationCenter = new TimeUtil();
    private double rotationOffset;
    private EntityLivingBase target;
    private EntityLivingBase rayCastedEntity;
    private float[] rotations;
    private long attackDelay;
    private int queuedAttacks;
    public boolean isBlocking;
    private boolean wasBlink;

    public boolean renderBlock;

    public KillAura() {
        super();
        addSettings(
                aimRange, attackRange, // range
                attackPattern, cps, randomization, // autoclicker
                autoBlock, blinkMode, blockRange, interactAutoblock, renderBlocking, // autoblock
                rotationRand, minRotationSpeed, maxRotationSpeed, // rotations
                moveFix, keepSprint, rayCast, // fixes
                noScaffold, noFlight, noEat, noBlock, // Checks
                sortMode);
    }

    @Override
    public void onEnable() {
        wasBlink = false;
        rotations = new float[]{mc.getPlayer().rotationYaw, mc.getPlayer().rotationPitch};
        attackDelay = AttackUtil.getAttackDelay(cps.getValue(), randomization.getValue(), attackPattern.getValue());
        queuedAttacks = 0;
        timer.reset();
        rotationCenter.reset();
    }

    @Override
    public void onDisable() {
        if(isBlocking) unblock();
        if(wasBlink) BlinkUtil.disable();
    }

    @Listen
    public void onRender(RenderEvent e) {
        if(e.getState() != RenderEvent.State.RENDER_2D) return;
        if (timer.hasReached(attackDelay) && target != null) {
            queuedAttacks++;
            timer.reset();
            attackDelay = AttackUtil.getAttackDelay(cps.getValue(), randomization.getValue(), attackPattern.getValue());
        }
    }

    @Listen
    public void onUpdate(UpdateEvent e) {

        if (noBlock.getValue() && mc.getPlayer().isUsingItem() && mc.getPlayer().getHeldItem().item instanceof ItemBlock) return;
        if (noScaffold.getValue() && Slack.getInstance().getModuleManager().getInstance(Scaffold.class).isToggle()) return;
        if (noEat.getValue() && mc.getPlayer().isUsingItem() && (mc.getPlayer().getHeldItem().item instanceof ItemFood ||mc.getPlayer().getHeldItem().item instanceof ItemBucketMilk || mc.getPlayer().isUsingItem() && (mc.getPlayer().getHeldItem().item instanceof ItemPotion))) return;
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
            rotations = new float[]{mc.getPlayer().rotationYaw, mc.getPlayer().rotationPitch};
            return;
        }

        if (mc.getPlayer().getDistanceToEntity(target) > aimRange.getValue()) return;
        renderBlock = canAutoBlock() && (renderBlocking.getValue() || isBlocking || autoBlock.getValue().equals("Fake")) && (!autoBlock.getValue().equals("None"));

        rotations = calculateRotations(target);

        RotationUtil.setClientRotation(rotations, 1);
        RotationUtil.setStrafeFix(moveFix.getValue(), false);

        if (mc.getPlayer().getDistanceToEntity(target) < blockRange.getValue() || isBlocking)
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

        mc.getPlayer().swingItem();

        if (mc.getPlayer().getDistanceToEntity(rayCastedEntity == null ? target : rayCastedEntity) > attackRange.getValue() + 0.3)
            return;

        if (keepSprint.getValue()) {
            mc.getPlayerController().syncCurrentPlayItem();
            PacketUtil.send(new C02PacketUseEntity(rayCastedEntity == null ? target : rayCastedEntity, C02PacketUseEntity.Action.ATTACK));
            if (mc.getPlayer().fallDistance > 0 && !mc.getPlayer().onGround) {
                mc.getPlayer().onCriticalHit(rayCastedEntity == null ? target : rayCastedEntity);
            }
        } else {
            mc.getPlayerController().attackEntity(mc.getPlayer(), rayCastedEntity == null ? target : rayCastedEntity);
        }
    }

    private boolean preAttack() {
        switch (autoBlock.getValue().toLowerCase()) {
            case "blatant":
                unblock();
                break;
            case "interact":
                if (mc.getPlayer().ticksExisted % 2 == 0) {
                    if (queuedAttacks > 0) {
                        unblock();
                    }
                    return true;
                } else {
                    return false;
                }
            case "basic":
                switch (mc.getPlayer().ticksExisted % 3) {
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
                        switch (mc.getPlayer().ticksExisted % 3) {
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
                        switch (mc.getPlayer().ticksExisted % 5) {
                            case 0:
                                unblock();
                                return true;
                            case 4:
                                block();
                                if (!BlinkUtil.isEnabled)
                                    BlinkUtil.enable(false, true);
                                BlinkUtil.setConfig(false, true);
                                BlinkUtil.releasePackets();
                                wasBlink = true;
                                return true;
                        }
                        break;
                    case "blatant":
                        switch (mc.getPlayer().ticksExisted % 2) {
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

    private void postAttack() {
        switch (autoBlock.getValue().toLowerCase()) {
            case "interact":
                block(true);
                break;
            case "blatant":
                block();
                break;
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
            rotationOffset = new SecureRandom().nextDouble();
            rotationCenter.reset();
        }

        final double distancedYaw = (entity.getDistanceToEntity(mc.getPlayer()) > attackRange.getValue() ? entity.getEyeHeight() : 2 * (entity.getDistanceToEntity(mc.getPlayer()) / 3.5));
        final float[] newRots = RotationUtil.getRotations(
                bb.minX + ((bb.maxX - bb.minX) / 2) + (rotationRand.getValue() ? (rotationOffset / 2) : 0),
                bb.minY + distancedYaw,
                bb.minZ + ((bb.maxZ - bb.minZ) / 2) + (rotationRand.getValue() ? (rotationOffset / 2) : 0));

        final float pitchSpeed = (float) (mc.getGameSettings().mouseSensitivity * MathUtil.getRandomInRange(minRotationSpeed.getValue(), maxRotationSpeed.getValue()));
        final float yawSpeed = (float) (mc.getGameSettings().mouseSensitivity * MathUtil.getRandomInRange(minRotationSpeed.getValue(), maxRotationSpeed.getValue()));

        newRots[0] = RotationUtil.updateRots(rotations[0], (float) MathUtil.getRandomInRange(newRots[0] - 2.19782323, newRots[0] + 2.8972343), pitchSpeed);
        newRots[1] = RotationUtil.updateRots(rotations[1], (float) MathUtil.getRandomInRange(newRots[1] - 3.13672842, newRots[1] + 3.8716793), yawSpeed);

        newRots[1] = MathHelper.clamp_float(newRots[1], -90, 90);

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
            //PacketUtil.send(new C02PacketUseEntity(targetEntity, RaycastUtil.rayCastHitVec(targetEntity, aimRange.getValue(), rotations)));
        }
        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.getPlayer().getCurrentEquippedItem()));
        isBlocking = true;
    }

    private void unblock() {
        if (!mc.getGameSettings().keyBindUseItem.isKeyDown())
            PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        else
            if (canAutoBlock()) mc.getGameSettings().keyBindUseItem.setPressed(false);
        isBlocking = false;
    }

    private boolean canAutoBlock() {
        return target != null && mc.getPlayer().getHeldItem() != null && mc.getPlayer().getHeldItem().getItem() instanceof ItemSword && mc.getPlayer().getDistanceToEntity(target) < blockRange.getValue();
    }

    @Override
    public String getMode() { return cps.getValue() + " cps" + ", " + autoBlock.getValue(); }
}
