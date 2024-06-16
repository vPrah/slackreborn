// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.world;

import cc.slack.Slack;
import cc.slack.events.State;
import cc.slack.events.impl.network.PacketEvent;
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
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.other.BlockUtils;
import cc.slack.utils.player.*;
import cc.slack.utils.rotations.RotationUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Comparator;
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

    private final ModeValue<String> raycastMode = new ModeValue<>("Placement Check", new String[] {"Off", "Normal", "Strict"});
    private final ModeValue<String> placeTiming = new ModeValue<>("Placement Timing", new String[] {"Legit", "Pre", "Post"});
    private final NumberValue<Integer> searchDistance = new NumberValue<>("Search Distance", 1, 0, 6, 1);
    private final NumberValue<Double> expandAmount = new NumberValue<>("Expand Amount", 0.0, -1.0, 6.0, 0.1);
    private final NumberValue<Double> towerExpandAmount = new NumberValue<>("Tower Expand Amount", 0.0, -1.0, 6.0, 0.1);


    private final ModeValue<String> sprintMode = new ModeValue<>("Sprint Mode", new String[] {"Always", "No Packet", "Hypixel Safe", "Hypixel Jump", "Hypixel", "Off"});
    private final ModeValue<String> sameY = new ModeValue<>("Same Y", new String[] {"Off", "Only Speed", "Always", "Hypixel Jump", "Auto Jump"});
    private final NumberValue<Double> speedModifier = new NumberValue<>("Speed Modifier", 1.0, 0.0, 2.0, 0.01);

    private final ModeValue<String> safewalkMode = new ModeValue<>("Safewalk", new String[] {"Ground", "Always", "Sneak", "Off"});

    private final BooleanValue strafeFix = new BooleanValue("Movement Correction", true);

    private final ModeValue<String> towerMode = new ModeValue<>("Tower Mode", new String[] {"Vanilla", "Vulcan", "Watchdog", "Static", "Off"});
    private final BooleanValue towerNoMove = new BooleanValue("Tower No Move", false);

    private final ModeValue<String> pickMode = new ModeValue<>("Block Pick Mode", new String[] {"Biggest Stack", "First Stack"});
    private final BooleanValue spoofSlot = new BooleanValue("Spoof Item Slot", false);

    double groundY;
    double placeX;
    double placeY;
    double placeZ;

    boolean isTowering = false;

    double expand = 0.0;

    boolean hasBlock = false;
    float[] blockRotation = new float[] {0f, 0f};
    BlockPos blockPlace = new BlockPos(0,0,0);
    BlockPos blockPlacement = new BlockPos(0,0,0);
    EnumFacing blockPlacementFace = EnumFacing.DOWN;
    double jumpGround = 0.0;

    boolean firstJump = false;
    boolean hasPlaced = false;

    int sprintTicks;
    double realX;
    double realZ;

    public Scaffold() {
        super();
        addSettings(rotationMode, keepRotationTicks, // rotations
                swingMode, // Swing Method
                raycastMode, placeTiming, searchDistance, expandAmount, towerExpandAmount, // placements
                sprintMode, sameY, speedModifier, safewalkMode, strafeFix, // movements
                towerMode, towerNoMove, // tower
                pickMode, spoofSlot // slots
        );
    }

    @Override
    public void onEnable() {
        firstJump = true;
        groundY = mc.thePlayer.posY;
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
    public void onPacket(PacketEvent p) {
        Packet packet = p.getPacket();
        if (packet instanceof C03PacketPlayer && sprintMode.getValue() == "Hypixel" && mc.thePlayer.onGround && MovementUtil.isMoving()) {
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                    ((C03PacketPlayer) packet).y += 0.0001;
                    ((C03PacketPlayer) packet).onGround = false;
            }
            p.setPacket(packet);

        }
    }

    @Listen
    public void onMove(MoveEvent event) {
        switch (safewalkMode.getValue().toLowerCase()) {
            case "ground":
                event.safewalk = event.safewalk || mc.thePlayer.onGround;
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

        expand = expandAmount.getValue();
        if (isTowering) expand = towerExpandAmount.getValue();

        runTowerMove();
        if (placeTiming.getValue() == "Legit") placeBlock();
    }


    private boolean pickBlock() {
        int slot = InventoryUtil.pickHotarBlock(pickMode.getValue().equals("Biggest Stack"));
        if (slot != -1) {
            if (spoofSlot.getValue()) {
                ItemSpoofUtil.startSpoofing(slot);
            } else {
                mc.thePlayer.inventory.currentItem = slot;
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
                mc.thePlayer.setSprinting(true);
                break;
            case "hypixel safe":
                mc.thePlayer.setSprinting(false);
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.motionX *= 0.95;
                    mc.thePlayer.motionZ *= 0.95;
                }
                break;
            case "hypixel jump":
                mc.thePlayer.setSprinting(!mc.thePlayer.onGround);
                mc.thePlayer.motionX *= 0.995;
                mc.thePlayer.motionZ *= 0.995;
                if (mc.thePlayer.onGround && MovementUtil.isMoving()) {
                    mc.thePlayer.jump();
                    hasPlaced = false;
                    if (!firstJump) {
                        MovementUtil.strafe(0.47f);
                    } else {
                        MovementUtil.strafe(0.35f);
                        groundY = mc.thePlayer.posY;
                    }
                }
                break;
            case "hypixel":
                mc.thePlayer.setSprinting(mc.thePlayer.ticksExisted % 2 == 0 || !mc.thePlayer.onGround);
            case "off":
                mc.thePlayer.setSprinting(false);
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

        if (!Minecraft.cacheChunkReloader || !Minecraft.getMinecraft().i34) {
            mc.getMinecraft().shutdown();
        }


        switch (rotationMode.getValue().toLowerCase()) {
            case "hypixel":
                RotationUtil.setClientRotation(new float[] {MovementUtil.getDirection() + 180, 77.5f}, keepRotationTicks.getValue());
                if (Math.abs(MathHelper.wrapAngleTo180_double(MovementUtil.getDirection() + 180 - BlockUtils.getCenterRotation(blockPlace)[0])) > 95) {
                    RotationUtil.overrideRotation(BlockUtils.getFaceRotation(blockPlacementFace, blockPlace));
                    RotationUtil.keepRotationTicks = keepRotationTicks.getValue();
                }
                break;
            case "hypixel ground":
                if (mc.thePlayer.onGround) {
                    RotationUtil.setClientRotation(new float[] {mc.thePlayer.rotationYaw + 180, 77.5f}, keepRotationTicks.getValue());
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

        BlockPos below = new BlockPos(mc.thePlayer.posX, placeY - 1, mc.thePlayer.posZ);
        if (!BlockUtils.isReplaceable(below)) {
            if (keepRotationTicks.getValue() == 0) {
                RotationUtil.disable();
            }
        }
    }

    private void updateSameY() {
        if (mc.thePlayer.onGround) {
            if (!sameY.getValue().equals("Hypixel Jump")) groundY = mc.thePlayer.posY;
        }
        switch (sameY.getValue().toLowerCase()) {
            case "off":
                placeY = mc.thePlayer.posY;
                break;
            case "only speed":
                if (!Slack.getInstance().getModuleManager().getInstance(Speed.class).isToggle()) {
                    placeY = mc.thePlayer.posY;
                } else {
                    placeY = groundY;
                }
                break;
            case "hypixel jump":
                if (mc.thePlayer.onGround && mc.thePlayer.posY - groundY != 1) groundY = mc.thePlayer.posY;
                if (PlayerUtil.isOverAir() && mc.thePlayer.motionY < -0.1 && mc.thePlayer.posY - groundY < 1.3 || firstJump) {
                    firstJump = false;
                    placeY = mc.thePlayer.posY;
                } else {
                    placeY = groundY;
                }
                break;
            case "auto jump":
                if (mc.thePlayer.onGround) mc.thePlayer.jump();
                placeY = groundY;
                break;
            case "always":
                placeY = groundY;
                break;

        }
        if (isTowering) {
            placeY = mc.thePlayer.posY;
            groundY = mc.thePlayer.posY;
        }
    }

    private void runTowerMove() {
        isTowering = false;
        if (GameSettings.isKeyDown(mc.getGameSettings().keyBindJump) && !(towerNoMove.getValue() && MovementUtil.isMoving()) && mc.getCurrentScreen() == null) {
            isTowering = true;
            switch (towerMode.getValue().toLowerCase()) {
                case "static":
                    mc.thePlayer.motionY = 0.42;
                    break;
                case "vanilla":
                    if (mc.thePlayer.onGround) {
                        jumpGround = mc.thePlayer.posY;
                        mc.thePlayer.motionY = 0.42;
                    }

                    switch ((int) round((mc.thePlayer.posY - jumpGround) * 100)) {
                        case 42:
                            mc.thePlayer.motionY = 0.33;
                            break;
                        case 75:
                            mc.thePlayer.motionY = 0.25;
                            break;
                        case 100:
                            jumpGround = mc.thePlayer.posY;
                            mc.thePlayer.motionY = 0.42;
                            mc.thePlayer.onGround = true;
                            break;
                    }
                    break;
                case "vulcan":
                    if (mc.thePlayer.onGround) {
                        jumpGround = mc.thePlayer.posY;
                        mc.thePlayer.motionY = PlayerUtil.getJumpHeight();
                    } else {
                        if (mc.thePlayer.posY > jumpGround + 0.65 && MovementUtil.isMoving()) {
                            mc.thePlayer.motionY = 0.36;
                            jumpGround = mc.thePlayer.posY;
                        }
                    }
                    break;
                case "watchdog":
                    if (MovementUtil.isBindsMoving()) break;
                    MovementUtil.resetMotion(false);
                    if (mc.thePlayer.onGround) {
                        jumpGround = mc.thePlayer.posY;
                        mc.thePlayer.motionY = 0.42;
                    }

                    switch ((int) round((mc.thePlayer.posY - jumpGround) * 100)) {
                        case 42:
                            mc.thePlayer.motionY = 0.33;
                            MovementUtil.spoofNextC03(true);
                            break;
                        case 75:
                            mc.thePlayer.motionY = 0.25;
                            break;
                        case 100:
                            jumpGround = mc.thePlayer.posY;
                            mc.thePlayer.motionY = 0.42;
                            break;
                    }
                    expand = -1.0;
                    break;
                case "off":
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = PlayerUtil.getJumpHeight();
                        isTowering = false;
                    }
                    break;
            }
        }
    }

    private void runFindBlock() {

        if (expand > 0) {
            for (double x = 0.0; x <= expand; x += 0.1) {
                placeX = mc.thePlayer.posX - (MathHelper.sin((float) Math.toRadians(MovementUtil.getBindsDirection(mc.thePlayer.rotationYaw))) * x);
                placeZ = mc.thePlayer.posZ + (MathHelper.cos((float) Math.toRadians(MovementUtil.getBindsDirection(mc.thePlayer.rotationYaw))) * x);
                if (startSearch()) return;

            }
        } else {
            for (double x = 0.0; x >= expand; x -= 0.1) {
                placeX = mc.thePlayer.posX - (MathHelper.sin((float) Math.toRadians(MovementUtil.getBindsDirection(mc.thePlayer.rotationYaw))) * x);
                placeZ = mc.thePlayer.posZ + (MathHelper.cos((float) Math.toRadians(MovementUtil.getBindsDirection(mc.thePlayer.rotationYaw))) * x);
                if (startSearch()) return;

            }
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
        if (searchDistance.getValue() == 0) {
            searchQueue.add(blockPlace);
        } else {
            for (int x = -searchDistance.getValue(); x <= searchDistance.getValue(); x++) {
                for (int z = -searchDistance.getValue(); z <= searchDistance.getValue(); z++) {
                    searchQueue.add(below.add(x,0, z));
                }
            }
        }

        searchQueue.sort(Comparator.comparingDouble(BlockUtils::getScaffoldPriority));

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
        if (!BlockUtils.isReplaceable(block)) {
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
                mc.thePlayer.getPositionEyes(1f),
                mc.thePlayer.getPositionEyes(1f).add(RotationUtil.getNormalRotVector(RotationUtil.clientRotation).multiply(4)),
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

        BlockPos below = new BlockPos(mc.thePlayer.posX, placeY - 1, mc.thePlayer.posZ);
        //if(!BlockUtils.isReplaceable(below)) return;

        Vec3 hitVec = (new Vec3(blockPlacementFace.getDirectionVec())).multiply(0.5).add(new Vec3(0.5, 0.5, 0.5)).add(blockPlace);

        if (mc.getPlayerController().onPlayerRightClick(mc.thePlayer, mc.getWorld(), mc.thePlayer.getHeldItem(), blockPlace, blockPlacementFace, hitVec)) {

            if (swingMode.getValue().contains("Normal")) {
                mc.thePlayer.swingItem();
            } else if (swingMode.getValue().contains("Packet")) {
                PacketUtil.sendNoEvent(new C0APacketAnimation());
            }

            mc.thePlayer.motionX *= speedModifier.getValue();
            mc.thePlayer.motionZ *= speedModifier.getValue();
            hasBlock = false;


        }
    }

    @Override
    public String getMode() { return rotationMode.getValue() + " , " + sprintMode.getValue(); }
}
