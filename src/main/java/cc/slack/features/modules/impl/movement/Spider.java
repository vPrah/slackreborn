package cc.slack.features.modules.impl.movement;

import cc.slack.events.State;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.TimeUtil;
import cc.slack.utils.player.MovementUtil;
import cc.slack.utils.player.PlayerUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

@ModuleInfo(
        name = "Spider",
        category = Category.MOVEMENT
)
public class Spider extends Module {

    private final ModeValue<String> spiderValue = new ModeValue<>(new String[]{"Normal", "Collide", "Jump", "Vulcan", "Verus"});

    private final NumberValue<Double> spiderSpeedValue = new NumberValue<>("Speed", 0.2D, 0.0D, 5.0D, 0.1D);

    private final TimeUtil timer = new TimeUtil();

    private boolean isMotionStop = true;
    private int vulcanTicks;

    public Spider() {
        addSettings(spiderValue, spiderSpeedValue);
    }


    @Listen
    public void onMotion (MotionEvent event) {
        if (event.getState() != State.PRE) return;

        switch (spiderValue.getValue()) {
            case "Normal":
                if (mc.getPlayer().isCollidedHorizontally) {
                    mc.getPlayer().motionY = spiderSpeedValue.getValue();
                    isMotionStop = false;
                } else if (!isMotionStop) {
                    mc.getPlayer().motionY = 0;
                    isMotionStop = true;
                }
                break;
            case "Collide":
                if (!mc.getPlayer().onGround && !mc.getGameSettings().keyBindSneak.isKeyDown()) {
                    if (mc.getPlayer().posY + mc.getPlayer().motionY < Math.floor(mc.getPlayer().posY))
                        if (!(PlayerUtil.getBlockRelativeToPlayer(-1, -1, 0) instanceof BlockAir) || !(PlayerUtil.getBlockRelativeToPlayer(1, -1, 0) instanceof BlockAir) || !(PlayerUtil.getBlockRelativeToPlayer(0, -1, -1) instanceof BlockAir) || !(PlayerUtil.getBlockRelativeToPlayer(0, -1, 1) instanceof BlockAir))
                            mc.getPlayer().motionY = (Math.floor(mc.getPlayer().posY) - (mc.getPlayer().posY));
                    if (mc.getPlayer().motionY == 0) {
                        mc.getPlayer().onGround = true;
                        event.setGround(true);
                    }
                }
                break;
            case "Jump":
                if (mc.getPlayer().isCollidedHorizontally && timer.hasReached(500L)) {
                    mc.getPlayer().motionY = MovementUtil.getJumpMotion(0.42F);
                    event.setGround(true);
                    timer.reset();
                }
                break;
            case "Vulcan":
                if (mc.getPlayer().onGround) {
                    vulcanTicks = 0;
                    mc.getPlayer().jump();
                }
                if (vulcanTicks >= 3) {
                    vulcanTicks = 0;
                }
                vulcanTicks++;
                switch (vulcanTicks) {
                    case 2:
                    case 3:
                        mc.getPlayer().jump();
                        MovementUtil.resetMotion(false);
                }
                break;

            case "Verus":
                break;
        }
    }

    @Listen
    public void onPacket(PacketEvent p) {
        Packet packet = p.getPacket();
        if (packet instanceof C03PacketPlayer && spiderValue.getValue() == "Vulcan") {
            switch (vulcanTicks) {
                case 2:
                    ((C03PacketPlayer) packet).onGround = true;
                    break;
                case 3:
                    ((C03PacketPlayer) packet).y -= 0.1;
                    ((C03PacketPlayer) packet).x -= MathHelper.sin(mc.getPlayer().rotationYaw) * 0.2;
                    ((C03PacketPlayer) packet).z += MathHelper.cos(mc.getPlayer().rotationYaw) * 0.2;
            }
            p.setPacket(packet);
        }
    }

    @Override
    public void onDisable() {
        isMotionStop = true;
        timer.reset();
        vulcanTicks = 0;
    }

    @Override
    public String getMode() { return spiderValue.getValue(); }



}
