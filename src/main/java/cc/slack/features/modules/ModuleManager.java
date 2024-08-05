// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.impl.combat.*;
import cc.slack.features.modules.impl.exploit.*;
import cc.slack.features.modules.impl.ghost.*;
import cc.slack.features.modules.impl.movement.*;
import cc.slack.features.modules.impl.other.*;
import cc.slack.features.modules.impl.player.*;
import cc.slack.features.modules.impl.render.*;
import cc.slack.features.modules.impl.utilties.*;
import cc.slack.features.modules.impl.world.*;

public class ModuleManager {
    public final Map<Class<? extends Module>, Module> modules = new LinkedHashMap<>();
    public final Map<Class<? extends Module>, Module> draggable = new LinkedHashMap<>();

    public void initialize() {
        try {
            addModules(
                    // DEV
                    //new UniversalFont(),
                    new BPSCounter(),
                    new FPSCounter(),
                    new XYZCounter(),



                    new ClientSpoofer(), // Dont remove
                    new AutoRod(), // Recoded// new CustomESP(), // Need Recode
                    new FreeLook(), // Need Recode


                    // Combat
                    new KillAura(),
                    new Velocity(), // Recoded
                    new Hitbox(),
                    new Criticals(), // Recoded
                    new AntiFireball(), // Made by dg636
                    new TickBase(),

                    // Movement
                    new Flight(),
                    new Speed(),
                    new NoSlow(),
                    new SafeWalk(),
                    new InvMove(),
                    new LongJump(), // Recoded
                    new Step(), // Recoded
                    new Sprint(),
                    new Strafe(),
                    new Glide(), // Recoded
                    new Jesus(), // Recoded
                    new Spider(),
                    new TargetStrafe(),
                    new NoWeb(), // Recoded
                    new VClip(), // Recoded

                    // Other
                    new AntiBot(),
                    new Performance(),
                    new RemoveEffect(),
                    new RichPresence(),
                    new Warns(), // Recoded
                    new FakePlayer(), // Recoded
                    new Killsults(),
                    new Targets(),
                    new Tweaks(),

                    // Player
                    new AntiVoid(), // Recoded
                    new NoFall(),
                    new Blink(),
                    new FastEat(),
                    new FreeLook(),
                    new FreeCam(),
                    new MCF(),
                    new TimerModule(),

                    // World
                    new Scaffold(),
                    new Breaker(),
                    new Stealer(),
                    new InvManager(),
                    new ChestAura(), // Recoded
                    new SpeedMine(),
                    new FastPlace(),

                    // Exploit
                    new Disabler(),
                    new Regen(),
                    new FastBow(),
                    new Phase(), // Recoded
                    new PingSpoof(),
                    new ChatBypass(),
                    new RageQuit(),
                    new MultiAction(),

                    // Render
                    new Interface(),
                    new ESP(),
                    new BasicESP(), // Recoded
                    new BedESP(), // Recoded
                    new ClickGUI(),
                    new NameTags(), // Recoded
                    new Animations(),
                    new TargetHUD(),
                    new KeyStrokes(), // Recoded
                    new Radar(), // Recoded
                    new Ambience(), // Recoded
                    new BlockOverlay(),
                    new Cape(),
                    new ChinaHat(),
                    new Cosmetics(), // Recoded
                    new Chams(), // Recoded
                    new ChestESP(), // Recoded
                    new Camera(), // Recoded
                    new ItemPhysics(), // Recoded
                    new Bobbing(), // Recoded
                    new Tracers(), // Recoded
                    new Projectiles(), // Recoded
                    new PointerESP(), // Recoded
                    new ScoreboardModule(),
                    new SessionInfo(), // Recoded
                    new Zoom(), // Recoded
                    new XRay(),

                    // Ghost
                    new SilentHitbox(),
                    new AimAssist(),
                    new Autoclicker(),
                    new Reach(),
                    new LegitScaffold(),
                    new JumpReset(),
                    new Wtap(),
                    new AutoTool(),
                    new Backtrack(),
                    new KeepSprint(),
                    new RealLag(),
                    new LegitNofall(),
                    new PearlAntiVoid(),

                    // Utilities
                    new AutoDisable(),
                    new AutoGapple(),
                    new AutoGG(), // Recoded
                    new AutoPlay(),
                    new AutoRespawn(),
                    new AutoLogin(), // Recoded
                    new AutoPot(),
                    new AutoSword(),
                    new AntiStaff(),
                    new AntiHarm(), // Recoded
                    new AutoCrafter(),
                    new NameProtect(), // Recoded
                    new TNTHelper(), // Recoded
                    new LagbackChecker()

            );
            
    		for(Module m : modules.values()) {
    			draggable.put(m.getClass(), m);
    		}
    		
        } catch (Exception e) {
            // Shut Up Exception
        }
    }

    public List<Module> getModules() {
        return new ArrayList<>(modules.values());
    }
    
    public List<Module> getDraggable() {
    	return new ArrayList<>(draggable.values());
    }

    public <T extends Module> T getInstance(Class<T> clazz) {
        return (T) modules.get(clazz);
    }

    public Module getModuleByName(String name) {
        for (Module mod : modules.values()) {
            if (mod.getName().equalsIgnoreCase(name))
                return mod;
        }


        throw new IllegalArgumentException("Module not found.");
    }

    private void addModules(Module... mod) {
        for (Module m : mod) {

            modules.put(m.getClass(), m);
        }
    }

    public Module[] getModulesByCategory(Category cat) {
        ArrayList<Module> category = new ArrayList<>();

        modules.forEach((aClass, mod) -> {
            if (mod.category == cat)
                category.add(mod);
        });

        return category.toArray(new Module[0]);
    }
}
