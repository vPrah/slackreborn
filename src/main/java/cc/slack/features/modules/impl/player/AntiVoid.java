// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.player;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.player.BlinkUtil;
import cc.slack.utils.network.PacketUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;


@ModuleInfo(
        name = "Antivoid",
        category = Category.PLAYER
)
public class AntiVoid extends Module {


    private final ModeValue<String> antivoidMode = new ModeValue<>(new String[] {"Universal", "Self TP", "Polar"});

    private double groundX = 0.0;
    private double groundY = 0.0;
    private double groundZ = 0.0;


    private boolean universalStarted = false;
    private boolean universalFlag = false;
    
    private boolean triedTP = false;

    public AntiVoid() {
        super();
        addSettings(antivoidMode);
    }

    @Override
    public void onEnable() {
        universalStarted = false;
    }

    @Override
    public void onDisable() {
        if (antivoidMode.getValue() == "Universal") BlinkUtil.disable();
    }
    

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate(UpdateEvent event) {
        switch (antivoidMode.getValue().toLowerCase()) {
            case "universal":
                if (universalStarted) {
                    if (mc.thePlayer.onGround || mc.thePlayer.fallDistance > 8f) {
                        BlinkUtil.disable();
                        universalStarted = false;
                        universalFlag = false;
                    } else if (mc.thePlayer.fallDistance > 4f && !universalFlag) {
                        universalFlag = true;
                        PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(groundX, groundY + 1, groundZ, false));
                    }
                } else if (mc.thePlayer.fallDistance > 0f && !mc.thePlayer.onGround && mc.thePlayer.motionY < 0) {
                    if (isOverVoid()) {
                        universalStarted = true;
                        universalFlag = false;
                        BlinkUtil.enable(false, true);
                        groundX = mc.thePlayer.posX;
                        groundY = mc.thePlayer.posY;
                        groundZ = mc.thePlayer.posZ;
                    }
                }
                break;
            case "self tp":
                if (mc.thePlayer.onGround) {
                    groundX = mc.thePlayer.posX;
                    groundY = mc.thePlayer.posY;
                    groundZ = mc.thePlayer.posZ;
                    triedTP = false;
                } else if (mc.thePlayer.fallDistance > 5f && !triedTP) {
                    mc.thePlayer.setPosition(groundX, groundY, groundZ);
                    mc.thePlayer.fallDistance = 0;
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.motionZ = 0;
                    triedTP = true;
                }

        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        switch (antivoidMode.getValue().toLowerCase()) {
            case "universal":
                if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                    if (((S08PacketPlayerPosLook) event.getPacket()).getX() == groundX && ((S08PacketPlayerPosLook) event.getPacket()).getY() == groundY && ((S08PacketPlayerPosLook) event.getPacket()).getZ() == groundZ) {
                        BlinkUtil.disable(false);
                        mc.thePlayer.setPosition(groundX, groundY, groundZ);
                        universalFlag = false;
                        universalStarted = false;
                    }
                }
                break;
            case "polar":
                if (event.getPacket() instanceof C03PacketPlayer) {
                    if (mc.thePlayer.fallDistance > 7 && isOverVoid()) {
                        event.cancel();
                    }
                }
        }
    }

    private boolean isOverVoid() {
        return mc.getWorld().rayTraceBlocks(
                new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ),
                new Vec3(mc.thePlayer.posX, mc.thePlayer.posY - 40, mc.thePlayer.posZ),
                true, true, false) == null;
    }

    @Override
    public String getMode() { return antivoidMode.getValue(); }

}
