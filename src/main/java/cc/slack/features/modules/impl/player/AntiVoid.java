// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.player;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.CollideEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.MoveEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.impl.player.antivoids.IAntiVoid;
import cc.slack.features.modules.impl.player.antivoids.impl.PolarAntiVoid;
import cc.slack.features.modules.impl.player.antivoids.impl.SelfTPAntiVoid;
import cc.slack.features.modules.impl.player.antivoids.impl.UniversalAntiVoid;
import cc.slack.utils.player.BlinkUtil;
import cc.slack.utils.network.PacketUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;


@ModuleInfo(
        name = "Antivoid",
        category = Category.PLAYER
)
public class AntiVoid extends Module {


    private final ModeValue<IAntiVoid> mode = new ModeValue<>(new IAntiVoid[] {new UniversalAntiVoid(), new SelfTPAntiVoid(), new PolarAntiVoid()});


    public AntiVoid() {
        super();
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        mode.getValue().onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
        mode.getValue().onDisable();
    }

    @Listen
    public void onMove(MoveEvent event) {
        mode.getValue().onMove(event);
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        mode.getValue().onUpdate(event);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        mode.getValue().onPacket(event);
    }

    @Listen
    public void onCollide(CollideEvent event) {
        mode.getValue().onCollide(event);
    }

    @Listen
    public void onMotion(MotionEvent event) {
        mode.getValue().onMotion(event);
    }

    @Override
    public String getMode() { return mode.getValue().toString(); }

}
