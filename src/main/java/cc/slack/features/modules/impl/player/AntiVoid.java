// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.player;

import cc.slack.events.State;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.player.BlinkUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;


@ModuleInfo(
        name = "Antivoid",
        category = Category.PLAYER
)
public class AntiVoid extends Module {


    private final ModeValue<String> antivoidMode = new ModeValue<>("mode", new String[] {"Universal", "Self TP", "Polar"});

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
                    if (mc.getPlayer().onGround) {
                        BlinkUtil.disable();
                        universalStarted = false;
                        universalFlag = false;
                    } else if (mc.getPlayer().fallDistance > 8 && !universalFlag) {
                        universalFlag = true;
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(groundX, groundY + 1, groundZ, false));
                    }
                } else if (mc.getPlayer().fallDistance > 0 && !mc.getPlayer().onGround && mc.getPlayer().motionY < 0) {
                    if (isOverVoid()) {
                        universalStarted = true;
                        universalFlag = false;
                        BlinkUtil.enable(false, true);
                        groundX = mc.getPlayer().posX;
                        groundY = mc.getPlayer().posY;
                        groundZ = mc.getPlayer().posZ;
                    }
                }
                break;
            case "selftp":
                if (mc.getPlayer().onGround) {
                    groundX = mc.getPlayer().posX;
                    groundY = mc.getPlayer().posY;
                    groundZ = mc.getPlayer().posZ;
                    triedTP = false;
                } else if (mc.getPlayer().fallDistance > 5 && !triedTP) {
                    mc.getPlayer().setPosition(groundX, groundY, groundZ);
                    mc.getPlayer().fallDistance = 0;
                    mc.getPlayer().motionX = 0;
                    mc.getPlayer().motionY = 0;
                    mc.getPlayer().motionZ = 0;
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
                        mc.getPlayer().setPosition(groundX, groundY, groundZ);
                        universalFlag = false;
                        universalStarted = false;
                    }
                }
                break;
        }
    }

    private boolean isOverVoid() {
        return mc.getWorld().rayTraceBlocks(
                new Vec3(mc.getPlayer().posX, mc.getPlayer().posY, mc.getPlayer().posZ),
                new Vec3(mc.getPlayer().posX, mc.getPlayer().posY - 40, mc.getPlayer().posZ),
                true, true, false) == null;
    }


}
