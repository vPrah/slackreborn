package cc.zenith.features.modules.impl.player;

import cc.zenith.events.impl.network.PacketEvent;
import cc.zenith.events.impl.player.MotionEvent;
import cc.zenith.events.impl.player.MoveEvent;
import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.impl.player.nofalls.INoFall;
import cc.zenith.features.modules.impl.player.nofalls.specials.VanillaNofall;
import cc.zenith.features.modules.impl.player.nofalls.specials.VerusNofall;
import cc.zenith.features.modules.impl.player.nofalls.specials.VulcanNofall;
import cc.zenith.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockLiquid;

@ModuleInfo(
        name = "NoFall",
        category = Category.PLAYER
)
public class NoFall extends Module {

    private final ModeValue<INoFall> mode = new ModeValue<>(new INoFall[]{new VanillaNofall(), new VulcanNofall(), new VerusNofall()});


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
}
