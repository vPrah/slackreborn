package cc.slack.features.modules.impl.movement.longjumps.impl.vulcan;

import cc.slack.Slack;
import cc.slack.events.State;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.impl.movement.LongJump;
import cc.slack.features.modules.impl.movement.longjumps.ILongJump;
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.player.MovementUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class VulcanLJ implements ILongJump {

    private boolean checking;
    private int ticks;


    @Override
    public void onMotion(MotionEvent event) {
        if (event.getState() != State.PRE) return;

        ticks++;

        if (mc.getPlayer().fallDistance > 0 && ticks % 2 == 0 && mc.getPlayer().fallDistance < 2.2) {
            mc.getPlayer().motionY += 0.14F;
        }

        switch (ticks) {
            case 1:
                mc.getTimer().timerSpeed = 0.5F;

                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY, mc.getPlayer().posZ, mc.getPlayer().onGround));
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY - 0.0784000015258789, mc.getPlayer().posZ, mc.getPlayer().onGround));
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY, mc.getPlayer().posZ, false));

                checking = true;
                MovementUtil.strafe(7.9F);
                mc.getPlayer().motionY = 0.42F;
                break;

            case 2:

                mc.getPlayer().motionY += 0.1F;
                MovementUtil.strafe(2.79F);
                break;

            case 3:
                MovementUtil.strafe(2.56F);
                break;

            case 4:
                event.setGround(true);
                mc.getPlayer().onGround = true;
                MovementUtil.strafe(0.49F);
                break;

            case 5:

                MovementUtil.strafe(0.59F);
                break;

            case 6:
                MovementUtil.resetMotion();
                MovementUtil.strafe(0.3F);
                break;
        }

        if (ticks > 6 && mc.getPlayer().onGround) Slack.getInstance().getModuleManager().getInstance(LongJump.class).toggle();
    }

    @Override
    public void onDisable() {
        MovementUtil.resetMotion();
        mc.getTimer().timerSpeed = 1F;
        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY - 78400000F, mc.getPlayer().posZ, mc.getPlayer().onGround));
    }

    @Override
    public void onEnable() {
        if (!mc.getPlayer().onGround) {
            Slack.getInstance().getModuleManager().getInstance(LongJump.class).toggle();
        }

        checking = false;
        ticks = 0;
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && checking) {
            event.cancel();
            checking = false;
        }
    }

    @Override
    public String toString() {
        return "Vulcan";
    }
}
