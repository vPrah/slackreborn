// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.player;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.MoveEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.impl.player.nofalls.INoFall;
import cc.slack.features.modules.impl.player.nofalls.basics.*;
import cc.slack.features.modules.impl.player.nofalls.specials.*;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;

@ModuleInfo(
        name = "NoFall",
        category = Category.PLAYER
)
public class NoFall extends Module {

    private final ModeValue<INoFall> mode = new ModeValue<>(new INoFall[]{
            // basic nofalls
            new VanillaNofall(),
            new SpoofGroundNofall(),
            new NoGroundNofall(),

            // special nofalls
            new HypixelBlinkNofall(),
            new VulcanNofall(),
            new VerusNofall()
    });


    public NoFall() {
        super();
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        mode.getValue().onEnable();
    }

    @Override
    public void onDisable() {
        mode.getValue().onDisable();
    }

    @Listen
    public void onMove(MoveEvent event) {
        mode.getValue().onMove(event);
    }


    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer().isSpectator() || mc.getPlayer().capabilities.allowFlying || mc.getPlayer().capabilities.disableDamage) {
            return;
        }


        mode.getValue().onUpdate(event);
    }

    @Listen
    public void onMotion(MotionEvent event) {
        mode.getValue().onMotion(event);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        mode.getValue().onPacket(event);
    }

    @Override
    public String getMode() { return mode.getValue().toString(); }
}
