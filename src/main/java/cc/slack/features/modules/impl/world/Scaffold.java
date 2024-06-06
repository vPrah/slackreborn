// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.world;

import cc.slack.Slack;
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
import cc.slack.features.modules.impl.movement.Speed;
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.other.BlockUtils;
import cc.slack.utils.player.*;
import cc.slack.utils.rotations.RotationUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
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

    private final ModeValue<String> rotationMode = new ModeValue<>("Rotation Mode", new String[] {"Vanilla", "Vanilla Center", "Hypixel", "Hypixel Ground"});
    private final NumberValue<Integer> keepRotationTicks = new NumberValue<>("Keep Rotation Length", 1, 0, 10, 1);
    private final ModeValue<String> swingMode = new ModeValue<>("Swing", new String[]{"Normal", "Packet", "None"});

    private final ModeValue<String> raycastMode = new ModeValue<>("Placement Check", new String[] {"Normal", "Strict", "Off"});
    private final ModeValue<String> placeTiming = new ModeValue<>("Placement Timing", new String[] {"Legit", "Pre", "Post"});
    private final NumberValue<Double> expandAmount = new NumberValue<>("Expand Amount", 0.0, 0.0, 6.0, 0.1);

    private final ModeValue<String> sprintMode = new ModeValue<>("Sprint Mode", new String[] {"Always", "No Packet", "Hypixel Safe", "Hypixel Jump", "Off"});
    private final ModeValue<String> sameY = new ModeValue<>("Same Y", new String[] {"Off", "Only Speed", "Always", "Hypixel Jump", "Auto Jump"});
    private final NumberValue<Double> speedModifier = new NumberValue<>("Speed Modifier", 1.0, 0.0, 2.0, 0.01);

    private final ModeValue<String> safewalkMode = new ModeValue<>("Safewalk", new String[] {"Ground", "Always", "Sneak", "Off"});

    private final BooleanValue strafeFix = new BooleanValue("Movement Correction", true);

    private final ModeValue<String> towerMode = new ModeValue<>("Tower Mode", new String[] {"Vanilla", "Vulcan", "Watchdog", "Static", "Off"});
    private final BooleanValue towerNoMove = new BooleanValue("Tower No Move", false);

    private final ModeValue<String> pickMode = new ModeValue<>("Block Pick Mode", new String[] {"Biggest Stack", "First Stack"});
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

    double groundY;
    double placeX;
    double placeY;
    double placeZ;

    boolean isTowering = false;

    boolean hasBlock = false;
    float[] blockRotation = new float[] {0f, 0f};
    BlockPos blockPlace = new BlockPos(0,0,0);
    BlockPos blockPlacement = new BlockPos(0,0,0);
    EnumFacing blockPlacementFace = EnumFacing.DOWN;
    double jumpGround = 0.0;

    boolean firstJump = false;
    boolean hasPlaced = false;

    public Scaffold() {
        super();
        addSettings(rotationMode, keepRotationTicks, // rotations
                swingMode, // Swing Method
                raycastMode, placeTiming, expandAmount, // placements
                sprintMode, sameY, speedModifier, safewalkMode, strafeFix, // movements
                towerMode, towerNoMove, // tower
                pickMode, spoofSlot // slots
        );
    }

    @Override
    public void onEnable() {
        firstJump = true;
        groundY = mc.getPlayer().posY;
    }

    @Override
    public void onDisable() {
        ItemSpoofUtil.stopSpoofing();
        RotationUtil.disable();
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
        updateSameY();
        runFindBlock();
        updatePlayerRotations();
        setMovementCorrection();
        runTowerMove();
        if (placeTiming.getValue() == "Legit") placeBlock();
    }


    private boolean pickBlock() {
        int slot = InventoryUtil.pickHotarBlock(pickMode.getValue().equals("Biggest Stack"));
        if (slot != -1) {
            if (spoofSlot.getValue()) {
                ItemSpoofUtil.startSpoofing(slot);
            } else {
                mc.getPlayer().inventory.currentItem = slot;
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
                if (mc.getPlayer().onGround) {
                    mc.getPlayer().motionX *= 0.95;
                    mc.getPlayer().motionZ *= 0.95;
                }
                break;
            case "hypixel jump":
                mc.getPlayer().setSprinting(!mc.getPlayer().onGround);
                mc.getPlayer().motionX *= 0.99;
                mc.getPlayer().motionZ *= 0.99;
                if (mc.getPlayer().onGround) {
                    mc.getPlayer().jump();
                    hasPlaced = false;
                    if (!firstJump) {
                        MovementUtil.strafe(0.47f);
                    } else {
                        MovementUtil.resetMotion(false);
                        groundY = mc.getPlayer().posY;
                    }
                }
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
                RotationUtil.setClientRotation(new float[] {mc.getPlayer().rotationYaw + 180, 77.5f}, keepRotationTicks.getValue());
                break;
            case "hypixel ground":
                if (mc.getPlayer().onGround) {
                    RotationUtil.setClientRotation(new float[] {mc.getPlayer().rotationYaw + 180, 77.5f}, keepRotationTicks.getValue());
                } else {
                    RotationUtil.setClientRotation(blockRotation, keepRotationTicks.getValue());
                }
                break;
            case "vanilla":
                RotationUtil.setClientRotation(BlockUtils.getFaceRotation(blockPlacementFace, blockPlace), keepRotationTicks.getValue());
                break;
            case "vanilla center":
                RotationUtil.setClientRotation(BlockUtils.getCenterRotation(blockPlace), keepRotationTicks.getValue());
                break;
        }

        BlockPos below = new BlockPos(mc.getPlayer().posX, placeY - 1, mc.getPlayer().posZ);
        if (!BlockUtils.isReplaceable(below)) {
            if (keepRotationTicks.getValue() == 0) {
                RotationUtil.disable();
            }
        }
    }

    private void updateSameY() {
        if (mc.getPlayer().onGround) {
            if (!sameY.getValue().equals("Hypixel Jump")) groundY = mc.getPlayer().posY;
        }
        switch (sameY.getValue().toLowerCase()) {
            case "off":
                placeY = mc.getPlayer().posY;
                break;
            case "only speed":
                if (!Slack.getInstance().getModuleManager().getInstance(Speed.class).isToggle()) {
                    placeY = mc.getPlayer().posY;
                } else {
                    placeY = groundY;
                }
                break;
            case "hypixel jump":
                if (mc.getPlayer().onGround && mc.getPlayer().posY - groundY != 1) groundY = mc.getPlayer().posY;
                if (PlayerUtil.isOverAir() && mc.getPlayer().motionY < -0.1 && mc.getPlayer().posY - groundY < 1.3 || firstJump) {
                    firstJump = false;
                    placeY = mc.getPlayer().posY;
                } else {
                    placeY = groundY;
                }
                break;
            case "auto jump":
                if (mc.getPlayer().onGround) mc.getPlayer().jump();
                placeY = groundY;
                break;
            case "always":
                placeY = groundY;
                break;

        }
        if (isTowering) {
            placeY = mc.getPlayer().posY;
            groundY = mc.getPlayer().posY;
        }
    }

    private void runTowerMove() {
        isTowering = false;
        if (GameSettings.isKeyDown(mc.getGameSettings().keyBindJump) && !(towerNoMove.getValue() && MovementUtil.isMoving()) && mc.getCurrentScreen() == null) {
            isTowering = true;
            switch (towerMode.getValue().toLowerCase()) {
                case "static":
                    mc.getPlayer().motionY = 0.42;
                    break;
                case "vanilla":
                    if (mc.getPlayer().onGround) {
                        jumpGround = mc.getPlayer().posY;
                        mc.getPlayer().motionY = 0.42;
                    }

                    switch ((int) round((mc.getPlayer().posY - jumpGround) * 100)) {
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
                case "watchdog":
                    MovementUtil.strafe(0.45f);
                    if (mc.getPlayer().onGround) {
                        mc.getPlayer().motionY = 0.4191;
                    }
                    switch (mc.getPlayer().offGroundTicks) {
                        case 1:
                            mc.getPlayer().motionY = 0.327318;
                            break;
                        case 5:
                            mc.getPlayer().motionY = -0.05;
                            break;
                        case 6:
                            mc.getPlayer().motionY = -1;
                            break;
                    }
                    break;
                case "off":
                    if (mc.getPlayer().onGround) {
                        mc.getPlayer().motionY = PlayerUtil.getJumpHeight();
                        isTowering = false;
                    }
                    break;
            }
        }
    }

    private void runFindBlock() {
        for (double x = 0.0; x <= expandAmount.getValue(); x += 0.1) {
            placeX = mc.getPlayer().posX - (MathHelper.sin((float) Math.toRadians(MovementUtil.getBindsDirection(mc.getPlayer().rotationYaw))) * x);
            placeZ = mc.getPlayer().posZ + (MathHelper.cos((float) Math.toRadians(MovementUtil.getBindsDirection(mc.getPlayer().rotationYaw))) * x);
            if (startSearch()) return;

        }
    }

    private boolean startSearch() {
        BlockPos below = new BlockPos(
                placeX,
                placeY - 1,
                placeZ);
        if(!BlockUtils.isReplaceable(below)) return false;

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
                return true;
            }
        }

        for (int i = 0; i < searchQueue.size(); i++)
        {
            if (searchBlock(searchQueue.get(i).down())) {
                hasBlock = true;
                return true;
            }
        }
        return false;
    }

    private boolean searchBlock(BlockPos block) {
        if (BlockUtils.isFullBlock(block)) {
            EnumFacing placeFace = BlockUtils.getHorizontalFacingEnum(block, placeX, placeZ);
            if (block.getY() <= placeY - 2) {
                placeFace = EnumFacing.UP;
            }
            blockPlacement = block.add(placeFace.getDirectionVec());
            if (!BlockUtils.isReplaceable(blockPlacement)) {
                return false;
            }
            blockRotation = BlockUtils.getFaceRotation(placeFace, block);
            blockPlace = block;
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
                    canContinue = raytraced.getBlockPos() == blockPlace;
                }
                break;
            case "strict":
                if (raytraced == null) {
                    canContinue = false;
                    break;
                }
                if (raytraced.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    canContinue = raytraced.getBlockPos() == blockPlace && raytraced.sideHit == blockPlacementFace;
                }
                break;
            default:
                break;
        }
        if (!canContinue) return;

        BlockPos below = new BlockPos(mc.getPlayer().posX, placeY - 1, mc.getPlayer().posZ);
        //if(!BlockUtils.isReplaceable(below)) return;

        Vec3 hitVec = (new Vec3(blockPlacementFace.getDirectionVec())).multiply(0.5).add(new Vec3(0.5, 0.5, 0.5)).add(blockPlace);

        if (mc.getPlayerController().onPlayerRightClick(mc.getPlayer(), mc.getWorld(), mc.getPlayer().getHeldItem(), blockPlace, blockPlacementFace, hitVec)) {

            if (swingMode.getValue().contains("Normal")) {
                mc.getPlayer().swingItem();
            } else if (swingMode.getValue().contains("Packet")) {
                PacketUtil.sendNoEvent(new C0APacketAnimation());
            }

            mc.getPlayer().motionX *= speedModifier.getValue();
            mc.getPlayer().motionZ *= speedModifier.getValue();
            hasBlock = false;


        }
    }

    @Override
    public String getMode() { return rotationMode.getValue() + " , " + sprintMode.getValue(); }
}
