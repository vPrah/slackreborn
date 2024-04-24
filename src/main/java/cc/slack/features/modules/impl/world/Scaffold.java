package cc.slack.features.modules.impl.world;

import cc.slack.events.State;
import cc.slack.events.impl.game.TickEvent;
import cc.slack.events.impl.player.JumpEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.BlockUtils;
import cc.slack.utils.player.MovementUtil;
import cc.slack.utils.player.PlayerUtil;
import cc.slack.utils.player.RotationUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import static java.lang.Math.round;


@ModuleInfo(
        name = "Scaffold",
        category = Category.WORLD,
        key = Keyboard.KEY_V
)
public class Scaffold extends Module {

    private final ModeValue<String> rotationMode = new ModeValue<>("Rotation Mode", new String[] {"Vanilla", "Hypixel"});
    private final NumberValue<Integer> keepRotationTicks = new NumberValue<>("Keep Rotation Length", 1, 0, 10, 1);
    private final ModeValue<String> sprintMode = new ModeValue<>("Sprint Mode", new String[] {"Always", "No Packet", "Hypixel Safe", "Off"});
    private final BooleanValue strafeFix = new BooleanValue("Movement Correction", true);
    private final ModeValue<String> towerMode = new ModeValue<>("Tower Mode", new String[] {"Vanilla", "Vulcan", "Off"});
    private final BooleanValue towerNoMove = new BooleanValue("Tower No Move", false);
    /*
    TODO:
    eagle / safewalk
    rotations mode backwards
    rotations raycast check
    impl towers
    setmotion / speed modifier (easy)
    place timing (easy)
    samey (should be easy)
    timer (easy)
    block counter (easy)
    spoof pick block (easy)
    switch block (med)
    expand (med) (shift / add search area)
    down scaffold (med) (play with samey ylevel and enum facing)

     */
    float yaw;

    float[] blockRotation = new float[] {0f, 0f};
    double jumpGround = 0.0;

    @Override
    public void onEnable() {

    }

    @Listen
    public void onMotion(MotionEvent event) {
        runTowerMove();
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        setSprint();
        setMovementCorrection();
        updatePlayerRotations();
    }

    private void setSprint() {
        switch (sprintMode.getValue().toLowerCase()) {
            case "always":
            case "no packet":
                mc.getPlayer().setSprinting(true);
                break;
            case "hypixel safe":
                mc.getPlayer().setSprinting(false);
                mc.getPlayer().motionX *= 0.95;
                mc.getPlayer().motionZ *= 0.95;
                break;
            case "off":
                mc.getPlayer().setSprinting(false);
                break;
        }
    }

    private void setMovementCorrection() {
        if (strafeFix.getValue()) {
            RotationUtil.setStrafeFix(true, false);
        } else {
            RotationUtil.setStrafeFix(false, false);
        }
    }

    private void updatePlayerRotations() {
        switch (rotationMode.getValue().toLowerCase()) {
            case "hypixel":
                RotationUtil.setClientRotation(new float[] {mc.getPlayer().rotationYaw + 180, 78f}, keepRotationTicks.getValue());
                break;
            case "vanilla":
                RotationUtil.setClientRotation(blockRotation, keepRotationTicks.getValue());
                break;
        }

        BlockPos below = new BlockPos(mc.getPlayer().posX, mc.getPlayer().posY - 1, mc.getPlayer().posZ);
        if (!BlockUtils.isReplaceable(below)) {
            if (keepRotationTicks.getValue() == 0) {
                RotationUtil.disable();
            }
        }
    }

    private void runTowerMove() {
        if (!GameSettings.isKeyDown(mc.getGameSettings().keyBindJump) || (towerNoMove.getValue() && MovementUtil.isMoving()))
        switch (towerMode.getValue().toLowerCase()) {
            case "vanilla":
                if (mc.getPlayer().onGround) {
                    jumpGround = mc.getPlayer().posY;
                    mc.getPlayer().motionY = 0.42;
                }

                switch ((int) round(mc.getPlayer().posY - jumpGround * 100)) {
                    case 42:
                        mc.getPlayer().motionY = 0.33;
                        break;
                    case 75:
                        mc.getPlayer().motionY = 0.25;
                        break;
                    case 100:
                        jumpGround = mc.getPlayer().posY;
                        mc.getPlayer().motionY = 0.42;
                        mc.getPlayer().onGround = true;
                        break;
                }
            case "vulcan":
                if (mc.getPlayer().onGround) {
                    jumpGround = mc.getPlayer().posY;
                    mc.getPlayer().motionY = PlayerUtil.getJumpHeight();
                }
                if (mc.getPlayer().posY > jumpGround + 0.65 && MovementUtil.isMoving()) {
                    mc.getPlayer().motionY = 0.36;
                    jumpGround = mc.getPlayer().posY;
                }
            case "off":
                if (mc.getPlayer().onGround) {
                    mc.getPlayer().motionY = PlayerUtil.getJumpHeight();
                }
                break;
        }
    }



//    @Listen
//    public void onTick(TickEvent event) {
//        if (event.getState() != State.PRE) return;
//        BlockPos below = new BlockPos(mc.getPlayer().posX, mc.getPlayer().posY - 1, mc.getPlayer().posZ);
//        if(mc.getWorld().getBlockState(below).getBlock().getMaterial() == Material.air) return;
//        EnumFacing facing = RotationUtil.getEnumDirection(yaw);
//
//        mc.getPlayerController().onPlayerRightClick(mc.getPlayer(), mc.getWorld(), mc.getPlayer().getHeldItem(), below, EnumFacing.WEST, new Vec3(0.5, 0.5, 0.5));
//    }
}