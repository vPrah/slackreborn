// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.utilties;

import cc.slack.Slack;
import cc.slack.features.modules.ModuleManager;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.impl.combat.Hitbox;
import cc.slack.features.modules.impl.combat.KillAura;
import cc.slack.features.modules.impl.combat.Velocity;
import cc.slack.features.modules.impl.ghost.*;
import cc.slack.features.modules.impl.movement.*;
import cc.slack.features.modules.impl.player.AntiVoid;
import cc.slack.features.modules.impl.player.FastEat;
import cc.slack.features.modules.impl.player.NoFall;
import cc.slack.features.modules.impl.world.Breaker;
import cc.slack.features.modules.impl.world.FastPlace;
import cc.slack.features.modules.impl.world.Scaffold;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "AutoConfig",
        category = Category.UTILITIES
)
public class AutoConfig extends Module {

    public final ModeValue<String> mode = new ModeValue<>(new String[]{"Hypixel", "Hypixel Ghost", "Vulcan", "Polar"});


    public AutoConfig() {
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        ModuleManager mm = Slack.getInstance().getModuleManager();
        KillAura ka = mm.getInstance(KillAura.class);
        Scaffold sc = mm.getInstance(Scaffold.class);
        Speed sp = mm.getInstance(Speed.class);
        Velocity vl = mm.getInstance(Velocity.class);

        final Module[] hypixelBlacklist = new Module[]{mm.getInstance(Flight.class), mm.getInstance(Step.class), mm.getInstance(FastEat.class), mm.getInstance(InvMove.class), mm.getInstance(Hitbox.class), mm.getInstance(Jesus.class)};
        final Module[] ghostBlacklist = new Module[]{ka, sc, mm.getInstance(NoFall.class), mm.getInstance(NoSlow.class), mm.getInstance(AntiVoid.class), mm.getInstance(Breaker.class)};


        switch (mode.getValue().toLowerCase()) {
            case "hypixel":
                ka.getValueByName("Attack Range").setValue(3.1D);
                ka.getValueByName("CPS").setValue(16);
                ka.getValueByName("Autoblock").setValue("Basic");
                ka.getValueByName("Interact").setValue(false);

                sc.getValueByName("Rotation Mode").setValue("Hypixel");
                sc.getValueByName("Keep Rotation Length").setValue(1);
                sc.getValueByName("Placement Check").setValue("Off");
                sc.getValueByName("Placement Timing").setValue("Legit");
                sc.getValueByName("Sprint Mode").setValue("Hypixel Safe");
                sc.getValueByName("Movement Correction").setValue(true);
                sc.getValueByName("Tower Mode").setValue("Off");

                sp.getValueByName("Mode: ").setValue("Hypixel Hop");
                sp.getValueByName("Jump Fix").setValue(true);

                mm.getInstance(AntiVoid.class).getValueByName("Mode: ").setValue("Universal");

                mm.getInstance(NoSlow.class).getValueByName("Bypass").setValue("Hypixel");

                mm.getInstance(Breaker.class).getValueByName("Bypass").setValue("Hypixel");

                mm.getInstance(NoFall.class).getValueByName("Mode: ").setValue("Hypixel Blink");

                vl.getValueByName("Mode: ").setValue("Hypixel");
                vl.getValueByName("Only Ground").setValue(false);

                for (Module m : hypixelBlacklist) {
                    m.setToggle(false);
                }
                break;
            case "hypixel ghost":
                for (Module m : hypixelBlacklist) {
                    m.setToggle(false);
                }

                for (Module m : ghostBlacklist) {
                    m.setToggle(false);
                }

                vl.setToggle(true);
                vl.getValueByName("Mode: ").setValue("Motion");
                vl.getValueByName("Vertical").setValue(100);
                vl.getValueByName("Horizontal").setValue(60);
                vl.getValueByName("Only Ground").setValue(false);

                mm.getInstance(Autoclicker.class).setToggle(true);
                mm.getInstance(AimAssist.class).setToggle(true);
                mm.getInstance(Backtrack.class).setToggle(false);
                mm.getInstance(Wtap.class).setToggle(true);

                mm.getInstance(Reach.class).getValueByName("Reach").setValue(3.1);
                mm.getInstance(Reach.class).getValueByName("Chance").setValue(0.5);

                mm.getInstance(FastPlace.class).setKey(Keyboard.KEY_V);
                mm.getInstance(LegitScaffold.class).setKey(Keyboard.KEY_V);

                mm.getInstance(FastPlace.class).setToggle(false);
                mm.getInstance(LegitScaffold.class).setToggle(false);

                break;
            case "polar":
                for (Module m : hypixelBlacklist) {
                    m.setToggle(false);
                }

                for (Module m : ghostBlacklist) {
                    m.setToggle(false);
                }

                vl.setToggle(false);

                mm.getInstance(Autoclicker.class).setToggle(true);
                mm.getInstance(Autoclicker.class).getValueByName("Target CPS").setValue(14f);
                mm.getInstance(Autoclicker.class).getValueByName("Randomization Pattern").setValue("PATTERN1");
                mm.getInstance(Autoclicker.class).getValueByName("Randomization Amount").setValue(2f);


                mm.getInstance(AimAssist.class).setToggle(true);
                mm.getInstance(Backtrack.class).setToggle(false);
                mm.getInstance(Wtap.class).setToggle(true);
                mm.getInstance(Reach.class).setToggle(false);
                mm.getInstance(Backtrack.class).setToggle(true);

                mm.getInstance(FastPlace.class).setKey(Keyboard.KEY_V);
                mm.getInstance(LegitScaffold.class).setKey(Keyboard.KEY_V);

                mm.getInstance(FastPlace.class).setToggle(false);
                mm.getInstance(LegitScaffold.class).setToggle(false);

                sp.getValueByName("Mode: ").setValue("Legit");

                break;

        }
        Slack.getInstance().addNotification("Loaded auto config: " + mode.getValue() + ".", " ", 4000L, Slack.NotificationStyle.SUCCESS);
        setToggle(false);
    }
}
