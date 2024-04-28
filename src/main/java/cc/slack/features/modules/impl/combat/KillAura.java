// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.combat;

import cc.slack.events.State;
import cc.slack.events.impl.game.TickEvent;
import cc.slack.events.impl.player.JumpEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.StrafeEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.other.MathUtil;
import cc.slack.utils.other.RaycastUtil;
import cc.slack.utils.other.TimeUtil;
import cc.slack.utils.player.AttackUtil;
import cc.slack.utils.player.PlayerUtil;
import cc.slack.utils.player.RotationUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;


import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@ModuleInfo(
        name = "KillAura",
        category = Category.COMBAT,
        key = Keyboard.KEY_R
)
public class KillAura extends Module {

    private final NumberValue<Double> aimRange = new NumberValue<>("Aim Range", 7.0D, 3.0D, 12.0D, 0.01D);
    private final NumberValue<Double> attackRange = new NumberValue<>("Attack Range", 3.0D, 3.0D, 7.0D, 0.01D);
    private final ModeValue<AttackUtil.AttackPattern> attackPattern = new ModeValue<>("Pattern", AttackUtil.AttackPattern.values());
    private final NumberValue<Integer> cps = new NumberValue<>("CPS", 18, 1, 30, 1);
    private final NumberValue<Double> randomization = new NumberValue<>("Randomization", 1.50D, 0D, 4D, 0.01D);
    private final ModeValue<String> autoBlock = new ModeValue<>("Autoblock", new String[]{"None", "Universocraft", "Blatant", "Vanilla"});
    // TODO: add universal / blink ab when can confirm blink utils are working
    private final BooleanValue rotationRand = new BooleanValue("Rotation Randomization", false);
    private final BooleanValue moveFix = new BooleanValue("Move Fix", false);
    private final BooleanValue keepSprint = new BooleanValue("Keep Sprint", true);
    private final BooleanValue rayCast = new BooleanValue("Ray Cast", true);

    private final ModeValue<String> sortMode = new ModeValue<>("Sort", new String[]{"FOV", "Distance", "Health", "Hurt Ticks"});
    private final BooleanValue teams = new BooleanValue("Teams", false);
    private final BooleanValue playerTarget = new BooleanValue("Players", true);
    private final BooleanValue animalTarget = new BooleanValue("Animals", true);
    private final BooleanValue mobsTarget = new BooleanValue("Mobs", true);

    private final TimeUtil timer = new TimeUtil();
    private final TimeUtil rotationCenter = new TimeUtil();
    private double rotationOffset;
    private EntityLivingBase target;
    private float[] rotations;
    private long attackDelay;
    private int queuedAttacks;
    private boolean isBlocking;

    public KillAura() {
        super();
        addSettings(aimRange, attackRange, attackPattern, cps, randomization, autoBlock, rotationRand, moveFix, keepSprint, rayCast, sortMode, teams, playerTarget, animalTarget, mobsTarget);
    }

    @Override
    public void onEnable() {
        rotations = new float[]{mc.getPlayer().rotationYaw, mc.getPlayer().rotationPitch};
        attackDelay = AttackUtil.getAttackDelay(cps.getValue(), randomization.getValue(), attackPattern.getValue());
        queuedAttacks = 0;
        timer.reset();
        rotationCenter.reset();
    }

    @Override
    public void onDisable() {
        if(isBlocking) unblock();
    }

    @Listen
    public void onStrafe(StrafeEvent e) {
        if (target != null && moveFix.getValue()) e.setYaw(rotations[0]);
    }

    @Listen
    public void onJump(JumpEvent e) {
        if (target != null && moveFix.getValue()) e.setYaw(rotations[0]);
    }

    @Listen
    public void onMotion(MotionEvent e) {
        if (target == null) {
            rotations[0] = mc.getPlayer().rotationYaw;
            rotations[1] = mc.getPlayer().rotationPitch;
        } else {
            e.setYaw(rotations[0]);
            e.setPitch(rotations[1]);
        }

        if(e.getState() == State.PRE) {
            if(canAutoBlock()) {
                switch (autoBlock.getValue()) {
                    case "Universocraft":
                        if (!mc.getGameSettings().keyBindUseItem.isKeyDown()) {
                            block();
                        }
                        break;
                }
            }
        }
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
    public void onTick(TickEvent e) {
        if(e.getState() == State.PRE) {
            target = getTarget();

            if (target == null) {
                attackDelay = 0;
                unblock();
                return;
            }

            if (mc.getPlayer().getDistanceToEntity(target) > aimRange.getValue()) return;

            rotations = calculateRotations(target);

            preAttack();
            while (queuedAttacks > 0) {
                attack(target);
                queuedAttacks--;
            }
            postAttack();
        } else {
            if(isBlocking && autoBlock.getValue().equalsIgnoreCase("Universocraft")) {
                isBlocking = false;
            }
        }
    }

    private void attack(EntityLivingBase target) {
        EntityLivingBase rayCastedEntity = null;
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

    private void preAttack() {
        switch (autoBlock.getValue().toLowerCase()) {
            case "blatant":
                unblock();
                break;
            default:
                break;
        }
    }

    private void postAttack() {
        switch (autoBlock.getValue().toLowerCase()) {
            case "blatant":
                block();
                break;
            case "vanilla":
                block();
            default:
                break;
        }
    }


    private EntityLivingBase getTarget() {
        if (mc.getPlayer() == null || mc.getWorld() == null) return null;
        List<EntityLivingBase> targets = new ArrayList<>();

        for (Entity entity : mc.getWorld().getLoadedEntityList().stream().filter(Objects::nonNull).collect(Collectors.toList())) {
            if (entity instanceof EntityLivingBase) {
                if (entity == mc.getPlayer()) continue;
                if (entity instanceof EntityArmorStand) continue;
                if (mobsTarget.getValue() && !(entity instanceof EntityMob)) continue;
                if (animalTarget.getValue() && !(entity instanceof EntityAnimal)) continue;
                if (playerTarget.getValue() && !(entity instanceof EntityPlayer)) continue;
                if (entity instanceof EntityPlayer && teams.getValue() && !PlayerUtil.isOnSameTeam((EntityPlayer) entity))
                    continue;
                if (mc.getPlayer().getDistanceToEntity(entity) > aimRange.getValue()) continue;
                targets.add((EntityLivingBase) entity);
            }
        }

        switch (sortMode.getValue().toLowerCase()) {
            case "fov":
                targets.sort(Comparator.comparingDouble(RotationUtil::getRotationDifference));
                break;
            case "distance":
                targets.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.getPlayer())));
                break;
            case "health":
                targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;
            case "hurt ticks":
                targets.sort(Comparator.comparingDouble(EntityLivingBase::getHurtTime));
                break;
        }

        return targets.isEmpty() ? null : targets.get(0);
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

        final float pitchSpeed = (float) (mc.getGameSettings().mouseSensitivity * MathUtil.getRandomInRange(65.0, 85.0));
        final float yawSpeed = (float) (mc.getGameSettings().mouseSensitivity * MathUtil.getRandomInRange(45.0, 65.0));

        newRots[0] = RotationUtil.updateRots(rotations[0], (float) MathUtil.getRandomInRange(newRots[0] - 2.19782323, newRots[0] + 2.8972343), pitchSpeed);
        newRots[1] = RotationUtil.updateRots(rotations[1], (float) MathUtil.getRandomInRange(newRots[1] - 3.13672842, newRots[1] + 3.8716793), yawSpeed);

        newRots[1] = MathHelper.clamp_float(newRots[1], -90, 90);

        return RotationUtil.applyGCD(newRots, rotations);
    }

    private void block() {
        if (isBlocking) return;
        // code for interact, recode to add it soon. mc.getPlayerController().interactWithEntitySendPacket(mc.getPlayer(), target);
        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.getPlayer().getCurrentEquippedItem()));
        isBlocking = true;
    }

    private void unblock() {
        if (!mc.getGameSettings().keyBindUseItem.isKeyDown())
            PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        else
            mc.getGameSettings().keyBindUseItem.setPressed(false);
        isBlocking = false;
    }

    private boolean canAutoBlock() {
        return target != null && mc.getPlayer().getHeldItem() != null && mc.getPlayer().getHeldItem().getItem() instanceof ItemSword;
    }
}