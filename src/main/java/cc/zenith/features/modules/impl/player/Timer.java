package cc.zenith.features.modules.impl.player;

import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.events.impl.render.RenderEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.features.modules.api.settings.impl.NumberValue;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.other.PrintUtil;
import cc.zenith.utils.other.TimeUtil;
import cc.zenith.utils.player.TimerUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
        name = "Timer",
        category = Category.PLAYER
)
public class Timer extends Module {

    private final ModeValue<String> mode = new ModeValue<>(new String[]{"Normal", "Balance"});
    private final NumberValue<Float> speed = new NumberValue<>("Speed", 1.5f, 0.1f, 10.0f, 0.25f);

    private int balance;
    private boolean balancing;
    private final TimeUtil timer = new TimeUtil();

    public Timer() {
        super();
        addSettings(mode, speed);
    }

    @Override
    public void onEnable() {
        balance = 0;
        balancing = false;
        timer.reset();
    }

    @Override
    public void onDisable() {
        TimerUtil.reset();
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        switch (mode.getValue().toLowerCase()) {
            case "balance":
                if (mc.getPlayer() != null && mc.getPlayer().ticksExisted % 20 == 0) {
                    PrintUtil.debugMessage("Balance: " + balance);
                }
                break;
            case "normal":
                mc.getTimer().timerSpeed = speed.getValue();
                break;
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (event.getDirection() != PacketDirection.INCOMING) return;

        if (mode.getValue().equalsIgnoreCase("balance")) {
            if (event.getPacket() instanceof C03PacketPlayer) {
                if (!(event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition ||
                        event.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook ||
                        event.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook)) {
                    balancing = true;
                    balance--;
                    event.cancel();
                } else {
                    balancing = false;
                    balance++;
                }
            }
        }
    }

    @Listen
    public void onRender(RenderEvent event) {
        if (event.state != RenderEvent.State.RENDER_2D) return;
        if (mode.getValue().equalsIgnoreCase("balance")) {
            if (timer.reach(50)) {
                balance--;
                timer.reset();
            }
        }
    }

}