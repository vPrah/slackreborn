// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.events.impl.player.MoveEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.features.modules.impl.movement.speeds.ISpeed;
import cc.slack.features.modules.impl.movement.speeds.hypixel.HypixelHopSpeed;
import cc.slack.features.modules.impl.movement.speeds.ncp.NCPHopSpeed;
import cc.slack.features.modules.impl.movement.speeds.ncp.OldNCPSpeed;
import cc.slack.features.modules.impl.movement.speeds.vanilla.*;
import cc.slack.features.modules.impl.movement.speeds.verus.VerusGroundSpeed;
import cc.slack.features.modules.impl.movement.speeds.verus.VerusHopSpeed;
import cc.slack.features.modules.impl.movement.speeds.verus.VerusLowHopSpeed;
import cc.slack.features.modules.impl.movement.speeds.vulcan.VulcanHopSpeed;
import cc.slack.features.modules.impl.movement.speeds.vulcan.VulcanLowSpeed;
import cc.slack.features.modules.impl.movement.speeds.vulcan.VulcanFrictionSpeed;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemFood;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "Speed",
        category = Category.MOVEMENT,
        key = Keyboard.KEY_B
)
public class Speed extends Module {

    private final ModeValue<ISpeed> mode = new ModeValue<>(new ISpeed[]{

            // Vanilla
            new VanillaSpeed(),
            new StrafeSpeed(),
            new GroundStrafeSpeed(),
            new LegitSpeed(),

            // Verus
            new VerusHopSpeed(),
            new VerusLowHopSpeed(),
            new VerusGroundSpeed(),

            // Hypixel
            new HypixelHopSpeed(),

            // NCP
            new NCPHopSpeed(),
            new OldNCPSpeed(),

            // Vulcan
            new VulcanLowSpeed(),
            new VulcanHopSpeed(),
            new VulcanFrictionSpeed()
    });

    public final NumberValue<Float> vanillaspeed = new NumberValue<>("Vanilla Speed", 1.0F, 0.0F, 3.0F, 0.01F);
    public final BooleanValue vanillaGround = new BooleanValue("Vanilla Only Ground", false);


    public final BooleanValue nosloweat = new BooleanValue("NoSlow when Speed", false);
    public final BooleanValue jumpFix = new BooleanValue("Jump Fix", true);

    public Speed() {
        super();
        addSettings(mode, vanillaspeed, vanillaGround, nosloweat, jumpFix);
    }

    @Override
    public void onEnable() {
        mode.getValue().onEnable();
    }

    @Override
    public void onDisable() {
        if (jumpFix.getValue()) { mc.getGameSettings().keyBindJump.pressed = GameSettings.isKeyDown(mc.getGameSettings().keyBindJump); }
        mode.getValue().onDisable();
    }

    @Listen
    public void onMove(MoveEvent event) {
        if (!nosloweat.getValue() && mc.getPlayer().isUsingItem() && (mc.getPlayer().getHeldItem().item instanceof ItemFood)) { return; }
        mode.getValue().onMove(event);
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (!nosloweat.getValue() && mc.getPlayer().isUsingItem() && (mc.getPlayer().getHeldItem().item instanceof ItemFood)) { return; }
        if (jumpFix.getValue()) { mc.getGameSettings().keyBindJump.pressed = false; }
        mode.getValue().onUpdate(event);
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (!nosloweat.getValue() && mc.getPlayer().isUsingItem() && (mc.getPlayer().getHeldItem().item instanceof ItemFood)) { return; }
        mode.getValue().onMotion(event);
    }

    @Listen
    public void onPacket(PacketEvent event) {

        mode.getValue().onPacket(event);
    }

    @Override
    public String getMode() {
        return mode.getValue().toString();
    }
}
