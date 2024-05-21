package cc.slack.features.modules.impl.ghost;

import cc.slack.events.State;
import cc.slack.events.impl.network.DisconnectEvent;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.PrintUtil;
import cc.slack.utils.player.PlayerUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;

@ModuleInfo(
        name = "PearlAntiVoid",
        category = Category.GHOST
)
public class PearlAntiVoid extends Module {

    private int overVoidTicks;
    private Vec3 position;
    private Vec3 motion;
    private boolean wasVoid;
    private boolean setBack;
    boolean shouldStuck;
    double x;
    double y;
    double z;
    boolean wait;
    private final NumberValue<Integer> fall = new NumberValue<>("Min fall distance", 5, 0, 10, 1);

    public PearlAntiVoid() {
        addSettings(fall);
    }

    @Override
    public void onDisable() {
        mc.getTimer().timerSpeed = 1.0f;
        mc.getPlayer().isDead = false;
    }


    @Listen
    public void onPacket(final PacketEvent e) {
        Packet<?> p = e.getPacket();
            if (e.getDirection() == PacketDirection.OUTGOING) {
                if (!mc.getPlayer().onGround && shouldStuck && p instanceof C03PacketPlayer
                        && !(p instanceof C03PacketPlayer.C05PacketPlayerLook)
                        && !(p instanceof C03PacketPlayer.C06PacketPlayerPosLook)) {
                    e.cancel();
                }
                if (p instanceof C08PacketPlayerBlockPlacement && wait) {
                    shouldStuck = false;
                    mc.getTimer().timerSpeed = 0.2f;
                    wait = false;
                }
            }
            if (e.getDirection() == PacketDirection.INCOMING) {
                if (p instanceof S08PacketPlayerPosLook) {
                    final S08PacketPlayerPosLook wrapper = (S08PacketPlayerPosLook) p;
                    x = wrapper.getX();
                    y = wrapper.getY();
                    z = wrapper.getZ();
                    mc.getTimer().timerSpeed = 0.2f;
                }
            }
    }

    @Listen
    public void onMotion(final MotionEvent e) {
        try {
            if (e.getState() != State.POST) {
                    if (mc.getPlayer().getHeldItem() == null) {
                        mc.getTimer().timerSpeed = 1.0f;
                    }

                    if (mc.getPlayer().getHeldItem().getItem() instanceof ItemEnderPearl) {
                        wait = true;
                    }

                    if (shouldStuck && !mc.getPlayer().onGround) {
                        mc.getPlayer().motionX = 0.0;
                        mc.getPlayer().motionY = 0.0;
                        mc.getPlayer().motionZ = 0.0;
                        mc.getPlayer().setPositionAndRotation(x, y, z, mc.getPlayer().rotationYaw,
                                mc.getPlayer().rotationPitch);
                    }
                    final boolean overVoid = !mc.getPlayer().onGround && !PlayerUtil.isBlockUnderP(30);
                    if (!overVoid) {
                        shouldStuck = false;
                        x = mc.getPlayer().posX;
                        y = mc.getPlayer().posY;
                        z = mc.getPlayer().posZ;
                        mc.getTimer().timerSpeed = 1.0f;
                    }
                    if (overVoid) {
                        ++overVoidTicks;
                    } else if (mc.getPlayer().onGround) {
                        overVoidTicks = 0;
                    }
                    if (overVoid && position != null && motion != null
                            && overVoidTicks < 30.0 + fall.getValue() * 20.0) {
                        if (!setBack) {
                            wasVoid = true;
                            if (mc.getPlayer().fallDistance > fall.getValue()) {
                                mc.getPlayer().fallDistance = 0.0f;
                                setBack = true;
                                shouldStuck = true;
                                x = mc.getPlayer().posX;
                                y = mc.getPlayer().posY;
                                z = mc.getPlayer().posZ;
                            }
                        }
                    } else {
                        if (shouldStuck) {
                            toggle();
                        }
                        shouldStuck = false;
                        mc.getTimer().timerSpeed = 1.0f;
                        setBack = false;
                        if (wasVoid) {
                            wasVoid = false;
                        }
                        motion = new Vec3(mc.getPlayer().motionX, mc.getPlayer().motionY, mc.getPlayer().motionZ);
                        position = new Vec3(mc.getPlayer().posX, mc.getPlayer().posY, mc.getPlayer().posZ);
                    }
            }
        } catch (NullPointerException ex) {

        }
    }

    @Listen
    public void onDisconnect (DisconnectEvent event) {
        toggle();
        PrintUtil.print("XD");
    }

}
