// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.world;

import cc.slack.events.State;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.MoveEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.BlockUtils;
import cc.slack.utils.player.*;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;


@ModuleInfo(
        name = "Scaffold",
        category = Category.WORLD,
        key = Keyboard.KEY_V
)
public class Scaffold extends Module {

    private final ModeValue<String> rotationMode = new ModeValue<>("Rotation Mode", new String[] {"Vanilla", "Hypixel"});
    private final NumberValue<Integer> keepRotationTicks = new NumberValue<>("Keep Rotation Length", 1, 0, 10, 1);

    private final ModeValue<String> raycastMode = new ModeValue<>("Placement Check", new String[] {"Normal", "Strict", "Off"});
    private final ModeValue<String> placeTiming = new ModeValue<>("Placement Timing", new String[] {"Legit", "Pre", "Post"});

    private final ModeValue<String> sprintMode = new ModeValue<>("Sprint Mode", new String[] {"Always", "No Packet", "Hypixel Safe", "Off"});
    private final NumberValue<Double> speedModifier = new NumberValue<>("Speed Modifier", 1.0, 0.0, 2.0, 0.01);

    private final ModeValue<String> safewalkMode = new ModeValue<>("Safewalk", new String[] {"Ground", "Always", "Sneak", "Off"});

    private final BooleanValue strafeFix = new BooleanValue("Movement Correction", true);

    private final ModeValue<String> towerMode = new ModeValue<>("Tower Mode", new String[] {"Vanilla", "Vulcan", "Static", "Off"});
    private final BooleanValue towerNoMove = new BooleanValue("Tower No Move", false);

    private final BooleanValue spoofSlot = new BooleanValue("Spoof Item Slot", false);


    /*
    TODO:
    eagle / safewalk √
    rotations mode backwards
    rotations raycast check √
    impl towers √
    setmotion / speed modifier (easy) √
    place timing (easy) √
    samey (should be easy)
    timer (easy)
    block counter (easy) √
    spoof pick block (easy) √
    switch block (med)
    expand (med) (shift / add search area)
    down scaffold (med) (play with samey ylevel and enum facing)

     */
    float yaw;

    boolean hasBlock = false;
    float[] blockRotation = new float[] {0f, 0f};
    BlockPos blockPlace = new BlockPos(0,0,0);
    BlockPos blockPlacement = new BlockPos(0,0,0);
    EnumFacing blockPlacementFace = EnumFacing.DOWN;
    double jumpGround = 0.0;

    public Scaffold() {
        super();
        addSettings(rotationMode, keepRotationTicks, raycastMode, placeTiming, sprintMode, speedModifier, safewalkMode, strafeFix, towerMode, towerNoMove, spoofSlot);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        ItemSpoofUtil.stopSpoofing();
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (event.getState() == State.PRE) {
            if (placeTiming.getValue() == "Pre") placeBlock();
        } else {
            if (placeTiming.getValue() == "Post") placeBlock();
        }
    }

    @Listen
    public void onMove(MoveEvent event) {
        switch (safewalkMode.getValue().toLowerCase()) {
            case "ground":
                event.safewalk = event.safewalk || mc.getPlayer().onGround;
                break;
            case "always":
                event.safewalk = true;
                break;
            case "sneak":
                mc.getGameSettings().keyBindSneak.pressed = PlayerUtil.isOverAir();
                break;
            default:
                break;

        }
    }

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate(UpdateEvent event) {
        if (!pickBlock()) {
            RotationUtil.disable();
            return;
        }
        setSprint();
        setMovementCorrection();
        runTowerMove();
        updatePlayerRotations();
        startSearch();
        if (placeTiming.getValue() == "Legit") placeBlock();
    }


    private boolean pickBlock() {
        if (InventoryUtil.pickHotarBlock(true) != -1) {
            if (spoofSlot.getValue()) {
                ItemSpoofUtil.startSpoofing(InventoryUtil.pickHotarBlock(true));
            } else {
                mc.getPlayer().inventory.currentItem = InventoryUtil.pickHotarBlock(true);
            }
            return true;
        }
        ItemSpoofUtil.stopSpoofing();
        return false;
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
        if (GameSettings.isKeyDown(mc.getGameSettings().keyBindJump) && !(towerNoMove.getValue() && MovementUtil.isMoving())) {
            switch (towerMode.getValue().toLowerCase()) {
                case "static":
                    mc.getPlayer().motionY = 0.42;
                    break;
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
                    break;
                case "vulcan":
                    if (mc.getPlayer().onGround) {
                        jumpGround = mc.getPlayer().posY;
                        mc.getPlayer().motionY = PlayerUtil.getJumpHeight();
                    }
                    if (mc.getPlayer().posY > jumpGround + 0.65 && MovementUtil.isMoving()) {
                        mc.getPlayer().motionY = 0.36;
                        jumpGround = mc.getPlayer().posY;
                    }
                    break;
                case "off":
                    if (mc.getPlayer().onGround) {
                        mc.getPlayer().motionY = PlayerUtil.getJumpHeight();
                    }
                    break;
            }
        }
    }

    private void startSearch() {
        BlockPos below = new BlockPos(mc.getPlayer().posX, mc.getPlayer().posY - 1, mc.getPlayer().posZ);
        List<BlockPos> searchQueue = new ArrayList<>();

        searchQueue.add(below.down());

        searchQueue.add(below.north());
        searchQueue.add(below.east());
        searchQueue.add(below.south());
        searchQueue.add(below.west());

        searchQueue.add(below.north().east());
        searchQueue.add(below.north().west());
        searchQueue.add(below.south().east());
        searchQueue.add(below.south().west());

        for (int i = 0; i < searchQueue.size(); i++)
        {
            if (searchBlock(searchQueue.get(i))) {
                hasBlock = true;
                return;
            }
        }

        for (int i = 0; i < searchQueue.size(); i++)
        {
            if (searchBlock(searchQueue.get(i).down())) {
                hasBlock = true;
                return;
            }
        }
    }

    private boolean searchBlock(BlockPos block) {
        if (BlockUtils.isFullBlock(block)) {
            EnumFacing placeFace = BlockUtils.getHorizontalFacingEnum(block);
            if (block.getY() <= mc.getPlayer().posY - 2) {
                placeFace = EnumFacing.UP;
            }
            blockRotation = BlockUtils.getFaceRotation(placeFace, block);
            blockPlace = block;
            blockPlacement = block.add(placeFace.getDirectionVec());
            if (!BlockUtils.isReplaceable(blockPlacement)) {
                return false;
            }
            blockPlacementFace = placeFace;
            return true;
        } else {
            return false;
        }
    }

    private void placeBlock() {
        if (!hasBlock) return;
        boolean canContinue = true;
        MovingObjectPosition raytraced = mc.getWorld().rayTraceBlocks(
                mc.getPlayer().getPositionEyes(1f),
                mc.getPlayer().getPositionEyes(1f).add(RotationUtil.getNormalRotVector(RotationUtil.clientRotation).multiply(4)),
                false, true, false);
        switch (raycastMode.getValue().toLowerCase()) {
            case "normal":
                if (raytraced == null) {
                    canContinue = false;
                    break;
                }
                if (raytraced.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
                    canContinue = false;
                } else {
                    canContinue = raytraced.getBlockPos() == blockPlacement;
                }
                break;
            case "strict":
                if (raytraced == null) {
                    canContinue = false;
                    break;
                }
                if (raytraced.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    canContinue = raytraced.getBlockPos() == blockPlacement && raytraced.sideHit == blockPlacementFace;
                }
                break;
            default:
                break;
        }
        if (!canContinue) return;

        BlockPos below = new BlockPos(mc.getPlayer().posX, mc.getPlayer().posY - 1, mc.getPlayer().posZ);
        if(!BlockUtils.isReplaceable(below)) return;

        Vec3 hitVec = (new Vec3(blockPlacementFace.getDirectionVec())).multiply(0.5).add(new Vec3(0.5, 0.5, 0.5));

        if (mc.getPlayerController().onPlayerRightClick(mc.getPlayer(), mc.getWorld(), mc.getPlayer().getHeldItem(), blockPlace, blockPlacementFace, hitVec)) {
            mc.getPlayer().swingItem();

            mc.getPlayer().motionX *= speedModifier.getValue();
            mc.getPlayer().motionZ *= speedModifier.getValue();
            hasBlock = false;
        }
    }
}